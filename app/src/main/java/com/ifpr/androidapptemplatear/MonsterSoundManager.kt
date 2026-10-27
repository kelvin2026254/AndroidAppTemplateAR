
package com.ifpr.androidapptemplatear

import android.content.Context
import android.media.MediaPlayer

class MonsterSoundManager(context: Context) {

    private val appContext = context.applicationContext
    private var player: MediaPlayer? = null

    fun playMonsterSound() {
        // Evita sobrepor sons se encontrar outro monstro
        player?.release()
        player = null

        player = MediaPlayer.create(
            appContext,
            R.raw.som_monstro
        )?.apply {
            setOnCompletionListener { mediaPlayer ->
                mediaPlayer.release()

                if (player === mediaPlayer) {
                    player = null
                }
            }

            start()
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
