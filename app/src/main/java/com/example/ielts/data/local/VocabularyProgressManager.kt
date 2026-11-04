package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ielts.data.datasource.VocabularyWord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Mức đánh giá ghi nhớ sau khi lật thẻ. */
enum class SrsGrade { AGAIN, HARD, EASY }

data class WordProgress(
    val level: Int = 0,
    val nextReviewMillis: Long = 0L,
    val lapses: Int = 0,
    val seen: Boolean = false
)

data class VocabularySummaryStats(
    val dueToday: Int,
    val mastered: Int,
    val learning: Int
)

/**
 * Lưu tiến độ học từ vựng theo thuật toán Spaced Repetition (SRS).
 *
 * - AGAIN: về level 0, ôn lại sau 1 phút (trong cùng phiên).
 * - HARD : giữ tối thiểu level 1, ôn lại sau 1 ngày.
 * - EASY : tăng 1 level, ôn lại sau 4 / 7 / 14 / 30 / 60 ngày.
 * Từ đạt level >= 3 được tính là "Đã thuộc".
 */
object VocabularyProgressManager {
    private const val PREFS_NAME = "vocabulary_srs_pref"
    private const val KEY_PROGRESS_MAP = "key_vocab_progress_map"
    private const val KEY_TOTAL_XP = "key_vocab_total_xp"
    private const val KEY_NEW_DATE = "key_vocab_new_date"
    private const val KEY_NEW_COUNT = "key_vocab_new_count"

    const val MASTERED_LEVEL = 3
    const val MAX_LEVEL = 5
    const val SESSION_SIZE = 20
    const val DAILY_NEW_LIMIT = 10

    private const val MINUTE_MS = 60_000L
    private const val DAY_MS = 24L * 60 * MINUTE_MS
    private val EASY_INTERVAL_DAYS = longArrayOf(4, 7, 14, 30, 60)

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(context: Context): Map<String, WordProgress> {
        val json = prefs(context).getString(KEY_PROGRESS_MAP, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, WordProgress>>() {}.type
            Gson().fromJson<Map<String, WordProgress>>(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getProgress(context: Context, wordId: String): WordProgress =
        getAll(context)[wordId] ?: WordProgress()

    fun isMastered(p: WordProgress): Boolean = p.seen && p.level >= MASTERED_LEVEL

    /** Số ngày ôn lại cho lần "Dễ" kế tiếp, dựa trên level hiện tại. */
    private fun easyIntervalDays(nextLevel: Int): Long =
        EASY_INTERVAL_DAYS[(nextLevel - 1).coerceIn(0, EASY_INTERVAL_DAYS.size - 1)]

    /** Nhãn khoảng thời gian dự kiến hiển thị dưới mỗi nút SRS. */
    fun previewIntervalLabel(current: WordProgress, grade: SrsGrade): String = when (grade) {
        SrsGrade.AGAIN -> "1 phút"
        SrsGrade.HARD -> "1 ngày"
        SrsGrade.EASY -> "${easyIntervalDays((current.level + 1).coerceAtMost(MAX_LEVEL))} ngày"
    }

    fun grade(context: Context, wordId: String, grade: SrsGrade, now: Long = System.currentTimeMillis()): WordProgress {
        val map = getAll(context).toMutableMap()
        val old = map[wordId] ?: WordProgress()

        if (!old.seen) registerNewWordToday(context)

        val updated = when (grade) {
            SrsGrade.AGAIN -> WordProgress(
                level = 0,
                nextReviewMillis = now + MINUTE_MS,
                lapses = old.lapses + 1,
                seen = true
            )
            SrsGrade.HARD -> {
                val level = old.level.coerceAtLeast(1)
                WordProgress(level, now + DAY_MS, old.lapses, true)
            }
            SrsGrade.EASY -> {
                val level = (old.level + 1).coerceAtMost(MAX_LEVEL)
                WordProgress(level, now + easyIntervalDays(level) * DAY_MS, old.lapses, true)
            }
        }
        map[wordId] = updated
        prefs(context).edit().putString(KEY_PROGRESS_MAP, Gson().toJson(map)).apply()
        return updated
    }

    private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun newWordsToday(context: Context): Int {
        val p = prefs(context)
        return if (p.getString(KEY_NEW_DATE, null) == todayKey()) p.getInt(KEY_NEW_COUNT, 0) else 0
    }

    private fun registerNewWordToday(context: Context) {
        val count = newWordsToday(context) + 1
        prefs(context).edit().putString(KEY_NEW_DATE, todayKey()).putInt(KEY_NEW_COUNT, count).apply()
    }

    fun addXp(context: Context, xp: Int) {
        val p = prefs(context)
        p.edit().putInt(KEY_TOTAL_XP, p.getInt(KEY_TOTAL_XP, 0) + xp).apply()
    }

    fun getTotalXp(context: Context): Int = prefs(context).getInt(KEY_TOTAL_XP, 0)

    /**
     * Hàng đợi học: từ đến hạn ôn (ưu tiên) rồi tới từ mới.
     * @param applyDailyNewCap giới hạn số từ mới mỗi ngày (dùng cho "Ôn tập ngay" tổng hợp).
     */
    fun buildQueue(
        context: Context,
        pool: List<VocabularyWord>,
        limit: Int = SESSION_SIZE,
        applyDailyNewCap: Boolean = false,
        isReviewMode: Boolean = false,
        now: Long = System.currentTimeMillis()
    ): List<VocabularyWord> {
        val progress = getAll(context)
        val due = pool.filter { val p = progress[it.id]; p != null && p.seen && p.nextReviewMillis <= now }
            .sortedBy { progress[it.id]!!.nextReviewMillis }

        if (isReviewMode) {
            // Chế độ Ôn tập: Tuyệt đối ưu tiên các từ người dùng ĐÃ TỪNG HỌC (seen == true)
            // 1. Các từ đã đến hạn (due)
            // 2. Các từ đang học nhưng chưa đến hạn (ưu tiên level thấp cần củng cố trước)
            val learnedNotDue = pool.filter { val p = progress[it.id]; p != null && p.seen && p.nextReviewMillis > now }
                .sortedWith(compareBy({ progress[it.id]?.level ?: 0 }, { progress[it.id]?.nextReviewMillis ?: 0L }))
            val reviewCandidates = (due + learnedNotDue).distinctBy { it.id }
            if (reviewCandidates.isNotEmpty()) {
                return reviewCandidates.take(limit)
            }
        }

        var newWords = pool.filter { progress[it.id]?.seen != true }
        if (applyDailyNewCap) {
            newWords = newWords.take((DAILY_NEW_LIMIT - newWordsToday(context)).coerceAtLeast(0))
        }
        val result = (due + newWords).toMutableList()
        // Nếu số lượng chưa đủ limit (hoặc khi đã học hết lượt mới hôm nay),
        // bổ sung các từ trong nhóm để người dùng luôn có thể luyện phản xạ bất kỳ lúc nào
        if (result.size < limit) {
            val remaining = pool.filter { it !in result }
                .sortedBy { progress[it.id]?.level ?: 0 }
            result.addAll(remaining)
        }
        return result.take(limit)
    }

    fun summaryStats(context: Context, pool: List<VocabularyWord>): VocabularySummaryStats {
        val progress = getAll(context)
        val mastered = pool.count { progress[it.id]?.let { p -> isMastered(p) } == true }
        val learning = pool.count { val p = progress[it.id]; p != null && p.seen && !isMastered(p) }
        val now = System.currentTimeMillis()
        val dueCount = pool.count { val p = progress[it.id]; p != null && p.seen && p.nextReviewMillis <= now }
        return VocabularySummaryStats(dueCount, mastered, learning)
    }

    /** Tiến độ 0–100 của một nhóm từ: trung bình level (tối đa MASTERED_LEVEL) của các từ. */
    fun progressPercent(context: Context, pool: List<VocabularyWord>): Int {
        if (pool.isEmpty()) return 0
        val progress = getAll(context)
        val sum = pool.sumOf { (progress[it.id]?.level ?: 0).coerceAtMost(MASTERED_LEVEL) }
        return (sum * 100) / (pool.size * MASTERED_LEVEL)
    }

    /** Số từ sẽ đến hạn ôn muộn nhất vào cuối ngày mai. */
    fun countDueByTomorrow(context: Context, now: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val limit = cal.timeInMillis
        return getAll(context).values.count { it.seen && it.nextReviewMillis <= limit }
    }
}

