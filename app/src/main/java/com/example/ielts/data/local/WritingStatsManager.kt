package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

data class WritingSummaryStats(
    val recentBand: Double,
    val bandDiff: String,
    val essaysWritten: Int,
    val targetBand: Double
)

data class SavedWritingRecord(
    val testId: String,
    val overallBand: Double,
    val taskAchievementBand: Double,
    val coherenceBand: Double,
    val lexicalBand: Double,
    val grammarBand: Double,
    val wordCount: Int,
    val timeSpentSeconds: Int,
    val resultJson: String?,
    val completedAtMillis: Long
)

object WritingStatsManager {
    private const val PREFS_NAME = "writing_practice_stats_pref"

    const val BASELINE_ESSAYS = 8
    const val BASELINE_BAND = 6.5
    const val BASELINE_BAND_DIFF = "+0.5"
    const val BASELINE_TARGET_BAND = 7.5

    private const val KEY_RECORDED_TESTS_MAP = "key_recorded_writing_map"
    private const val KEY_RECENT_BAND = "key_recent_band"
    private const val KEY_PREV_BAND = "key_prev_band"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getRecords(context: Context): Map<String, SavedWritingRecord> {
        val json = getPrefs(context).getString(KEY_RECORDED_TESTS_MAP, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, SavedWritingRecord>>() {}.type
            Gson().fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun recordTestCompletion(
        context: Context,
        testId: String,
        overallBand: Double,
        taskAchievementBand: Double,
        coherenceBand: Double,
        lexicalBand: Double,
        grammarBand: Double,
        wordCount: Int,
        timeSpentSeconds: Int,
        resultJson: String?
    ) {
        val prefs = getPrefs(context)
        val records = getRecords(context).toMutableMap()

        val record = SavedWritingRecord(
            testId = testId,
            overallBand = overallBand,
            taskAchievementBand = taskAchievementBand,
            coherenceBand = coherenceBand,
            lexicalBand = lexicalBand,
            grammarBand = grammarBand,
            wordCount = wordCount,
            timeSpentSeconds = timeSpentSeconds,
            resultJson = resultJson,
            completedAtMillis = System.currentTimeMillis()
        )
        records[testId] = record

        val prevBand = prefs.getFloat(KEY_RECENT_BAND, BASELINE_BAND.toFloat())

        prefs.edit()
            .putString(KEY_RECORDED_TESTS_MAP, Gson().toJson(records))
            .putFloat(KEY_PREV_BAND, prevBand)
            .putFloat(KEY_RECENT_BAND, overallBand.toFloat())
            .apply()
    }

    fun getSummaryStats(context: Context): WritingSummaryStats {
        val prefs = getPrefs(context)
        val records = getRecords(context)

        val recentBand = if (records.isNotEmpty()) {
            val latest = records.values.maxByOrNull { it.completedAtMillis }
            latest?.overallBand ?: BASELINE_BAND
        } else {
            prefs.getFloat(KEY_RECENT_BAND, BASELINE_BAND.toFloat()).toDouble()
        }

        val prevBand = prefs.getFloat(KEY_PREV_BAND, (recentBand - 0.5).toFloat()).toDouble()
        val diff = recentBand - prevBand
        val diffStr = if (diff >= 0) String.format(Locale.US, "+%.1f", diff) else String.format(Locale.US, "%.1f", diff)

        val totalEssays = BASELINE_ESSAYS + records.size

        return WritingSummaryStats(
            recentBand = recentBand,
            bandDiff = diffStr,
            essaysWritten = totalEssays,
            targetBand = BASELINE_TARGET_BAND
        )
    }

    fun getRecordForTest(context: Context, testId: String): SavedWritingRecord? {
        return getRecords(context)[testId]
    }
}

