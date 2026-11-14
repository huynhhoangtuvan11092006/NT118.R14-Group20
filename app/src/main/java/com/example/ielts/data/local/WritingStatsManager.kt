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

    private const val KEY_RECORDED_TESTS_MAP = "key_recorded_writing_map"
    private const val KEY_RECENT_BAND = "key_recent_band"
    private const val KEY_PREV_BAND = "key_prev_band"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getRecords(context: Context): Map<String, SavedWritingRecord> {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_RECORDED_TESTS_MAP, null)
        val existing: Map<String, SavedWritingRecord> = if (json != null) {
            try {
                val type = object : TypeToken<Map<String, SavedWritingRecord>>() {}.type
                Gson().fromJson<Map<String, SavedWritingRecord>>(json, type) ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }
        if (existing.isNotEmpty()) return existing

        // Fallback recovery from writing_practice_prefs if main stats map was previously cleared
        val legacyPrefs = context.getSharedPreferences("writing_practice_prefs", Context.MODE_PRIVATE)
        val recovered = mutableMapOf<String, SavedWritingRecord>()
        for ((key, value) in legacyPrefs.all) {
            if (key.startsWith("saved_result_") && value is String && value.isNotBlank()) {
                val testId = key.removePrefix("saved_result_")
                try {
                    val obj = org.json.JSONObject(value)
                    val overall = obj.optDouble("estimatedBand", obj.optDouble("overallBand", 0.0))
                    if (overall > 0.0) {
                        recovered[testId] = SavedWritingRecord(
                            testId = testId,
                            overallBand = overall,
                            taskAchievementBand = obj.optDouble("taskAchievementBand", overall),
                            coherenceBand = obj.optDouble("coherenceBand", overall),
                            lexicalBand = obj.optDouble("lexicalBand", overall),
                            grammarBand = obj.optDouble("grammarBand", overall),
                            wordCount = obj.optInt("wordCount", 150),
                            timeSpentSeconds = obj.optInt("timeSpentSeconds", 900),
                            resultJson = value,
                            completedAtMillis = obj.optLong("completedAtMillis", System.currentTimeMillis())
                        )
                    }
                } catch (_: Exception) {
                }
            }
        }
        if (recovered.isNotEmpty()) {
            val latestBand = recovered.values.maxByOrNull { it.completedAtMillis }?.overallBand?.toFloat() ?: 0f
            prefs.edit()
                .putString(KEY_RECORDED_TESTS_MAP, Gson().toJson(recovered))
                .putFloat(KEY_RECENT_BAND, latestBand)
                .commit()
        }
        return recovered
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

        val prevBand = prefs.getFloat(KEY_RECENT_BAND, 0f)

        prefs.edit()
            .putString(KEY_RECORDED_TESTS_MAP, Gson().toJson(records))
            .putFloat(KEY_PREV_BAND, prevBand)
            .putFloat(KEY_RECENT_BAND, overallBand.toFloat())
            .commit()

        StreakManager.recordActivity(context, "WRITING")
    }

    fun getSummaryStats(context: Context): WritingSummaryStats {
        val prefs = getPrefs(context)
        val records = getRecords(context)

        val recentBand = if (records.isNotEmpty()) {
            val latest = records.values.maxByOrNull { it.completedAtMillis }
            latest?.overallBand ?: 0.0
        } else {
            0.0
        }

        val prevBand = prefs.getFloat(KEY_PREV_BAND, 0f).toDouble()
        val diff = if (prevBand > 0) recentBand - prevBand else 0.0
        val diffStr = if (recentBand <= 0.0) {
            "Chưa có điểm"
        } else if (prevBand > 0 && diff != 0.0) {
            if (diff > 0) String.format(Locale.US, "+%.1f", diff) else String.format(Locale.US, "%.1f", diff)
        } else {
            "Mới nhất"
        }

        val totalEssays = records.size

        return WritingSummaryStats(
            recentBand = recentBand,
            bandDiff = diffStr,
            essaysWritten = totalEssays,
            targetBand = 7.5
        )
    }

    fun getRecordForTest(context: Context, testId: String): SavedWritingRecord? {
        return getRecords(context)[testId]
    }

    fun getLatestRecord(context: Context): SavedWritingRecord? {
        return getRecords(context).values.maxByOrNull { it.completedAtMillis }
    }

    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
        context.getSharedPreferences("writing_practice_prefs", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("writing_drafts_pref", Context.MODE_PRIVATE).edit().clear().apply()
    }
}

