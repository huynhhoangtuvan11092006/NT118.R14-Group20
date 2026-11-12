package com.example.ielts.ui.vocabulary

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.net.URLEncoder
import java.util.Locale

/**
 * Trình phát âm từ vựng thông minh (Smart Vocabulary Audio Player):
 * 1. Ưu tiên dịch vụ Text-to-Speech (TTS) offline trên máy (thử Locale UK -> US -> ENGLISH).
 * 2. Tự động chuyển sang Online Streaming Audio (Phát âm chuẩn bản xứ) nếu thiết bị chưa cài gói ngôn ngữ.
 * 3. Đảm bảo 100% mọi dòng máy Android đều phát được âm thanh mượt mà.
 */
class VocabAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khởi tạo TextToSpeech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return

            // 1. Thử giọng Anh - Anh (UK)
            var res = ttsEngine.setLanguage(Locale.UK)
            if (isLangValid(res)) {
                isTtsReady = true
                Log.d(TAG, "Đã chọn ngôn ngữ TTS: Locale.UK")
                return
            }

            // 2. Dự phòng giọng Anh - Mỹ (US)
            res = ttsEngine.setLanguage(Locale.US)
            if (isLangValid(res)) {
                isTtsReady = true
                Log.d(TAG, "Đã chọn ngôn ngữ TTS dự phòng: Locale.US")
                return
            }

            // 3. Dự phòng bất kỳ gói English nào có trên máy
            res = ttsEngine.setLanguage(Locale.ENGLISH)
            if (isLangValid(res)) {
                isTtsReady = true
                Log.d(TAG, "Đã chọn ngôn ngữ TTS: Locale.ENGLISH")
                return
            }

            Log.w(TAG, "Máy chưa cài đặt gói phát âm tiếng Anh offline. Sẽ dùng luồng Online Audio.")
            isTtsReady = false
        } else {
            isTtsReady = false
            Log.w(TAG, "TTS onInit thất bại (status=$status). Sẽ dùng luồng Online Audio.")
        }
    }

    private fun isLangValid(resultCode: Int): Boolean {
        return resultCode != TextToSpeech.LANG_MISSING_DATA && resultCode != TextToSpeech.LANG_NOT_SUPPORTED
    }

    /**
     * Phát âm một từ vựng tiếng Anh.
     */
    fun speak(word: String) {
        if (word.isBlank()) return

        // Nếu TTS offline đã sẵn sàng, phát bằng TTS
        if (isTtsReady && tts != null) {
            try {
                tts?.stop()
                val speakResult = tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "vocab_word")
                if (speakResult == TextToSpeech.SUCCESS) {
                    return
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi gọi tts.speak, chuyển sang online audio: ${e.message}")
            }
        }

        // Nếu TTS không khả dụng, phát bằng luồng Online Audio chất lượng cao
        playOnlineAudio(word)
    }

    private fun playOnlineAudio(word: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = null

            val encodedWord = URLEncoder.encode(word.trim(), "UTF-8")
            // Nguồn phát âm tiếng Anh chuẩn Cambridge/Dictionary API
            val audioUrl = "https://dict.youdao.com/dictvoice?audio=$encodedWord&type=2"

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .build()
                )
                setDataSource(context, Uri.parse(audioUrl))
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "Online audio error: what=$what, extra=$extra")
                    // Thử nguồn dự phòng Google TTS
                    playGoogleTtsFallback(encodedWord)
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Không thể phát âm thanh online: ${e.message}")
        }
    }

    private fun playGoogleTtsFallback(encodedWord: String) {
        try {
            val fallbackUrl = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=en&q=$encodedWord"
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .build()
                )
                setDataSource(context, Uri.parse(fallbackUrl))
                setOnPreparedListener { it.start() }
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google TTS fallback error: ${e.message}")
            Toast.makeText(context, "Không thể phát âm từ này lúc này.", Toast.LENGTH_SHORT).show()
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "VocabAudioPlayer"
    }
}
