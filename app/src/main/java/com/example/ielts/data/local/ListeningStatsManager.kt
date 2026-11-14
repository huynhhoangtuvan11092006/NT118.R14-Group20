package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ielts.data.remote.dto.UserProgressDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

data class ListeningSummaryStats(
    val recentBand: Double,
    val bandDiff: String,
    val testsPracticed: Int,
    val sectionsPracticed: Int,
    val accuracyAvg: Int
)

data class SavedListeningTestRecord(
    val testId: String,
    val score: Int,
    val totalQuestions: Int,
    val bandEstimate: Double,
    val resultJson: String?,
    val sectionsJson: String?,
    val completedAtMillis: Long
)

object ListeningStatsManager {
    private const val PREFS_NAME = "listening_practice_stats_pref"

    const val BASELINE_TESTS = 12
    const val BASELINE_SECTIONS = 48
    const val BASELINE_BAND = 7.5
    const val BASELINE_BAND_DIFF = "+0.5"
    const val BASELINE_ACCURACY = 82

    private const val KEY_RECORDED_TESTS_MAP = "key_listening_recorded_tests_map"
    private const val KEY_RECENT_BAND = "key_listening_recent_band"
    private const val KEY_PREV_BAND = "key_listening_prev_band"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getRecords(context: Context): Map<String, SavedListeningTestRecord> {
        val json = getPrefs(context).getString(KEY_RECORDED_TESTS_MAP, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, SavedListeningTestRecord>>() {}.type
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
        sectionsJson: String?
    ) {
        val prefs = getPrefs(context)
        val currentRecords = getRecords(context).toMutableMap()

        val record = SavedListeningTestRecord(
            testId = testId,
            score = score,
            totalQuestions = totalQuestions,
            bandEstimate = bandEstimate,
            resultJson = resultJson,
            sectionsJson = sectionsJson,
            completedAtMillis = System.currentTimeMillis()
        )
        currentRecords[testId] = record

        val prevRecent = prefs.getFloat(KEY_RECENT_BAND, BASELINE_BAND.toFloat())

        prefs.edit()
            .putString(KEY_RECORDED_TESTS_MAP, Gson().toJson(currentRecords))
            .putFloat(KEY_PREV_BAND, prevRecent)
            .putFloat(KEY_RECENT_BAND, bandEstimate.toFloat())
            .commit()

        StreakManager.recordActivity(context, "LISTENING")
    }

    fun getSummaryStats(context: Context): ListeningSummaryStats {
        val records = getRecords(context).values.sortedBy { it.completedAtMillis }

        if (records.isEmpty()) {
            return ListeningSummaryStats(
                recentBand = 0.0,
                bandDiff = "+0.0",
                testsPracticed = 0,
                sectionsPracticed = 0,
                accuracyAvg = 0
            )
        }

        val latest = records.last()
        val recentBand = latest.bandEstimate
        val prevBand = if (records.size >= 2) records[records.size - 2].bandEstimate else latest.bandEstimate
        val diff = recentBand - prevBand
        val diffStr = when {
            diff > 0 -> String.format(Locale.US, "+%.1f", diff)
            diff < 0 -> String.format(Locale.US, "%.1f", diff)
            else -> "+0.0"
        }

        val testsCount = records.size
        val sectionsCount = records.size * 4
        val totalQuestions = records.sumOf { it.totalQuestions.coerceAtLeast(1) }
        val totalCorrect = records.sumOf { it.score }
        val accuracyAvg = if (totalQuestions > 0) ((totalCorrect.toDouble() / totalQuestions) * 100).toInt().coerceIn(0, 100) else 0

        return ListeningSummaryStats(
            recentBand = recentBand,
            bandDiff = diffStr,
            testsPracticed = testsCount,
            sectionsPracticed = sectionsCount,
            accuracyAvg = accuracyAvg
        )
    }

    fun getUserProgressForTest(context: Context, testId: String): UserProgressDto? {
        val record = getRecords(context)[testId] ?: return null
        return UserProgressDto(
            submissionId = "local-${record.testId}",
            highestScore = record.score.toFloat(),
            bandEstimate = record.bandEstimate.toFloat(),
            lastAttemptAt = record.completedAtMillis.toString()
        )
    }

    fun getSavedResultForTest(context: Context, testId: String): SavedListeningTestRecord? {
        return getRecords(context)[testId]
    }

    fun getLatestRecord(context: Context): SavedListeningTestRecord? {
        val records = getRecords(context).values.toList()
        return records.maxByOrNull { it.completedAtMillis }
    }
}

