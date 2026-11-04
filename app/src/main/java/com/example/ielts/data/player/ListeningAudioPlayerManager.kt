package com.example.ielts.data.player

import android.content.Context
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
import androidx.media3.exoplayer.ExoPlayer

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
                    progressHandler.postDelayed(this, 500)
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

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build().apply {
                volume = 1.0f
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Log.d(tag, "PlaybackState changed to: $playbackState (READY=3, BUFFERING=2, ENDED=4)")
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                listener?.onStateChanged(isPlaying = false, isBuffering = true)
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

        exoPlayer?.let { player ->
            player.volume = 1.0f
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.playbackParameters = PlaybackParameters(currentSpeed)
            player.prepare()
            if (autoPlay) {
                player.play()
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
