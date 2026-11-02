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
            .apply()
    }

    fun getStats(context: Context): ReadingSummaryStats {
        val prefs = getPrefs(context)
        val records = getRecords(context).values.toList()

        if (records.isEmpty()) {
            return ReadingSummaryStats(
                recentBand = BASELINE_BAND,
                bandDiff = BASELINE_BAND_DIFF,
                testsPracticed = BASELINE_TESTS,
                passagesPracticed = BASELINE_PASSAGES,
                accuracyAvg = BASELINE_ACCURACY
            )
        }

        // Most recent test record by completion time
        val latestRecord = records.maxByOrNull { it.completedAtMillis }!!
        val recentBand = latestRecord.bandEstimate

        // Diff calculation compared to previous band
        val prevBand = prefs.getFloat(KEY_PREV_BAND, BASELINE_BAND.toFloat()).toDouble()
        val diffVal = recentBand - prevBand
        val bandDiffStr = when {
            diffVal > 0 -> String.format(Locale.US, "+%.1f", diffVal)
            diffVal < 0 -> String.format(Locale.US, "%.1f", diffVal)
            else -> "+0.0"
        }

        // Tests count and passages count
        val testsCount = BASELINE_TESTS + records.size
        val passagesCount = testsCount * 3

        // Accurate average accuracy
        var totalAccuracySum = BASELINE_ACCURACY.toDouble() * BASELINE_TESTS
        for (rec in records) {
            val acc = if (rec.totalQuestions > 0) (rec.score.toDouble() / rec.totalQuestions * 100) else 0.0
            totalAccuracySum += acc
        }
        val avgAccuracy = (totalAccuracySum / testsCount).toInt().coerceIn(0, 100)

        return ReadingSummaryStats(
            recentBand = recentBand,
            bandDiff = bandDiffStr,
            testsPracticed = testsCount,
            passagesPracticed = passagesCount,
            accuracyAvg = avgAccuracy
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
