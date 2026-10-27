
package com.ifpr.androidapptemplatear

import android.content.Context
import android.media.MediaPlayer

class AudioManagerGame(context: Context) {

    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(
                appContext,
                R.raw.som_ambiente
            )?.apply {
                isLooping = true
            }
        }

        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
