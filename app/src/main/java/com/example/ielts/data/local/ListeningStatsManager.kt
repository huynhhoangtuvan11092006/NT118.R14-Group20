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
            .apply()
    }

    fun getSummaryStats(context: Context): ListeningSummaryStats {
        val prefs = getPrefs(context)
        val records = getRecords(context)

        val testsCount = BASELINE_TESTS + records.size
        val sectionsCount = BASELINE_SECTIONS + (records.size * 4)

        val recentBandFloat = prefs.getFloat(KEY_RECENT_BAND, BASELINE_BAND.toFloat())
        val prevBandFloat = prefs.getFloat(KEY_PREV_BAND, (BASELINE_BAND - 0.5).toFloat())

        val diff = recentBandFloat - prevBandFloat
        val diffStr = if (diff >= 0) {
            String.format(Locale.US, "+%.1f", diff)
        } else {
            String.format(Locale.US, "%.1f", diff)
        }

        val accuracyAvg = if (records.isNotEmpty()) {
            val totalAccuracy = records.values.sumOf { (it.score.toDouble() / it.totalQuestions.coerceAtLeast(1)) * 100 }
            ((BASELINE_ACCURACY * BASELINE_TESTS + totalAccuracy) / testsCount).toInt()
        } else {
            BASELINE_ACCURACY
        }

        return ListeningSummaryStats(
            recentBand = recentBandFloat.toDouble(),
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

