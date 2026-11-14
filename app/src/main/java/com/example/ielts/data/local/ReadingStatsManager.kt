package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ielts.data.remote.dto.UserProgressDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

data class ReadingSummaryStats(
    val recentBand: Double,
    val bandDiff: String,
    val testsPracticed: Int,
    val passagesPracticed: Int,
    val accuracyAvg: Int
)

data class SavedTestRecord(
    val testId: String,
    val score: Int,
    val totalQuestions: Int,
    val bandEstimate: Double,
    val resultJson: String?,
    val passagesJson: String?,
    val completedAtMillis: Long
)

object ReadingStatsManager {
    private const val PREFS_NAME = "reading_practice_stats_pref"

    // Default baseline values matching app design
    const val BASELINE_TESTS = 12
    const val BASELINE_PASSAGES = 36
    const val BASELINE_BAND = 7.0
    const val BASELINE_BAND_DIFF = "+0.5"
    const val BASELINE_ACCURACY = 78

    private const val KEY_RECORDED_TESTS_MAP = "key_recorded_tests_map"
    private const val KEY_RECENT_BAND = "key_recent_band"
    private const val KEY_PREV_BAND = "key_prev_band"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getRecords(context: Context): Map<String, SavedTestRecord> {
        val json = getPrefs(context).getString(KEY_RECORDED_TESTS_MAP, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, SavedTestRecord>>() {}.type
            Gson().fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun recordTestCompletion(
        context: Context,
        testId: String,
        score: Int,
        totalQuestions: Int,
        bandEstimate: Double,
        resultJson: String?,
        passagesJson: String?
    ) {
        val prefs = getPrefs(context)
        val currentRecords = getRecords(context).toMutableMap()

        val record = SavedTestRecord(
            testId = testId,
            score = score,
            totalQuestions = totalQuestions,
            bandEstimate = bandEstimate,
            resultJson = resultJson,
            passagesJson = passagesJson,
            completedAtMillis = System.currentTimeMillis()
        )
        currentRecords[testId] = record

        // Get previous band before updating
        val prevBand = prefs.getFloat(KEY_RECENT_BAND, BASELINE_BAND.toFloat()).toDouble()

        prefs.edit()
            .putString(KEY_RECORDED_TESTS_MAP, Gson().toJson(currentRecords))
            .putFloat(KEY_PREV_BAND, prevBand.toFloat())
            .putFloat(KEY_RECENT_BAND, bandEstimate.toFloat())
            .commit()

        StreakManager.recordActivity(context, "READING")
    }

    fun getStats(context: Context): ReadingSummaryStats {
        val records = getRecords(context).values.sortedBy { it.completedAtMillis }

        if (records.isEmpty()) {
            return ReadingSummaryStats(
                recentBand = 0.0,
                bandDiff = "+0.0",
                testsPracticed = 0,
                passagesPracticed = 0,
                accuracyAvg = 0
            )
        }

        val latest = records.last()
        val recentBand = latest.bandEstimate
        val prevBand = if (records.size >= 2) records[records.size - 2].bandEstimate else latest.bandEstimate
        val diff = recentBand - prevBand
        val bandDiffStr = when {
            diff > 0 -> String.format(Locale.US, "+%.1f", diff)
            diff < 0 -> String.format(Locale.US, "%.1f", diff)
            else -> "+0.0"
        }

        val testsCount = records.size
        val passagesCount = testsCount * 3
        val totalQuestions = records.sumOf { it.totalQuestions.coerceAtLeast(1) }
        val totalCorrect = records.sumOf { it.score }
        val accuracyAvg = if (totalQuestions > 0) ((totalCorrect.toDouble() / totalQuestions) * 100).toInt().coerceIn(0, 100) else 0

        return ReadingSummaryStats(
            recentBand = recentBand,
            bandDiff = bandDiffStr,
            testsPracticed = testsCount,
            passagesPracticed = passagesCount,
            accuracyAvg = accuracyAvg
        )
    }

    fun getUserProgressForTest(context: Context, testId: String): UserProgressDto? {
        val record = getRecords(context)[testId] ?: return null
        return UserProgressDto(
            submissionId = "sub_${record.completedAtMillis}",
            highestScore = record.score.toFloat(),
            bandEstimate = record.bandEstimate.toFloat(),
            lastAttemptAt = "Vừa xong"
        )
    }

    fun getSavedResultForTest(context: Context, testId: String): SavedTestRecord? {
        return getRecords(context)[testId]
    }

    fun getLatestRecord(context: Context): SavedTestRecord? {
        val records = getRecords(context).values.toList()
        return records.maxByOrNull { it.completedAtMillis }
    }
}
