package com.example.ielts.data.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

interface AudioPlayerListener {
    fun onProgress(currentMs: Long, durationMs: Long)
    fun onStateChanged(isPlaying: Boolean, isBuffering: Boolean)
    fun onPlaybackCompleted()
    fun onError(message: String)
}

@OptIn(UnstableApi::class)
class ListeningAudioPlayerManager(private val context: Context) {

    private val tag = "ListeningAudioPlayer"
    private var exoPlayer: ExoPlayer? = null
    private var listener: AudioPlayerListener? = null
    private var currentUrl: String? = null
    private var currentSpeed: Float = 1.0f

    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            exoPlayer?.let { player ->
                val current = player.currentPosition
                val duration = player.duration.coerceAtLeast(0L)
                listener?.onProgress(current, duration)
                if (player.isPlaying) {
                    progressHandler.postDelayed(this, 300)
                }
            }
        }
    }

    init {
        initPlayer()
    }

    private fun initPlayer() {
        if (exoPlayer != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                50_000,  // minBufferMs: tải trước 50 giây
                120_000, // maxBufferMs: tối đa 2 phút
                2_500,   // bufferForPlaybackMs
                5_000    // bufferForPlaybackAfterRebufferMs
            )
            .setBackBuffer(30_000, true)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setLoadControl(loadControl)
            .build().apply {
                volume = 1.0f
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Log.d(tag, "PlaybackState changed to: $playbackState (READY=3, BUFFERING=2, ENDED=4)")
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                listener?.onStateChanged(isPlaying = isPlaying(), isBuffering = true)
                            }
                            Player.STATE_READY -> {
                                listener?.onStateChanged(isPlaying = isPlaying, isBuffering = false)
                                if (isPlaying) startProgressTicker()
                            }
                            Player.STATE_ENDED -> {
                                listener?.onStateChanged(isPlaying = false, isBuffering = false)
                                listener?.onPlaybackCompleted()
                                stopProgressTicker()
                            }
                            Player.STATE_IDLE -> {
                                listener?.onStateChanged(isPlaying = false, isBuffering = false)
                                stopProgressTicker()
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(tag, "onIsPlayingChanged: $isPlaying")
                        listener?.onStateChanged(isPlaying = isPlaying, isBuffering = false)
                        if (isPlaying) {
                            startProgressTicker()
                        } else {
                            stopProgressTicker()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(tag, "ExoPlayer Error: ${error.message}", error)
                        listener?.onError(error.message ?: "Lỗi phát audio")
                        stopProgressTicker()
                    }
                })
            }
    }

    fun setListener(listener: AudioPlayerListener?) {
        this.listener = listener
    }

    private fun getCacheFileForUrl(url: String): File {
        val safeName = "audio_cache_" + Math.abs(url.hashCode()).toString(16) + ".mp3"
        return File(context.cacheDir, safeName)
    }

    fun playUrl(url: String, autoPlay: Boolean = true) {
        if (url.isBlank()) return

        Log.d(tag, "playUrl requested: $url, autoPlay: $autoPlay")

        if (url == currentUrl && exoPlayer != null) {
            if (autoPlay && !isPlaying()) {
                resume()
            }
            return
        }

        currentUrl = url
        initPlayer()

        val cacheFile = getCacheFileForUrl(url)
        val playUri = if (cacheFile.exists() && cacheFile.length() > 500_000L) {
            Log.d(tag, "Playing directly from local cached file: ${cacheFile.absolutePath} (${cacheFile.length()} bytes)")
            Uri.fromFile(cacheFile)
        } else {
            Log.d(tag, "Playing from remote URL and starting background cache: $url")
            startBackgroundDownload(url, cacheFile)
            Uri.parse(url)
        }

        exoPlayer?.let { player ->
            player.volume = 1.0f
            val mediaItem = MediaItem.fromUri(playUri)
            player.setMediaItem(mediaItem)
            player.playbackParameters = PlaybackParameters(currentSpeed)
            player.prepare()
            if (autoPlay) {
                player.play()
            }
        }
    }

    private fun startBackgroundDownload(url: String, targetFile: File) {
        thread(isDaemon = true, name = "AudioCacheDownloader") {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 20000
                connection.connect()
                if (connection.responseCode in 200..299) {
                    val tempFile = File(context.cacheDir, targetFile.name + ".tmp")
                    connection.inputStream.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (tempFile.length() > 500_000L) {
                        tempFile.renameTo(targetFile)
                        Log.d(tag, "Successfully cached audio to disk: ${targetFile.absolutePath} (${targetFile.length()} bytes)")
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Background audio caching skipped: ${e.message}")
            }
        }
    }

    fun resume() {
        exoPlayer?.volume = 1.0f
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun togglePlayPause() {
        if (isPlaying()) {
            pause()
        } else {
            resume()
        }
    }

    fun isPlaying(): Boolean = exoPlayer?.isPlaying == true

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs.coerceAtLeast(0L))
    }

    fun seekRelative(offsetMs: Long) {
        exoPlayer?.let { player ->
            val target = (player.currentPosition + offsetMs).coerceIn(0L, player.duration.coerceAtLeast(0L))
            player.seekTo(target)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        currentSpeed = speed
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    fun getPlaybackSpeed(): Float = currentSpeed

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L

    fun getDuration(): Long = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L

    private fun startProgressTicker() {
        stopProgressTicker()
        progressHandler.post(progressRunnable)
    }

    private fun stopProgressTicker() {
        progressHandler.removeCallbacks(progressRunnable)
    }

    fun release() {
        stopProgressTicker()
        exoPlayer?.release()
        exoPlayer = null
        listener = null
        currentUrl = null
    }
}
