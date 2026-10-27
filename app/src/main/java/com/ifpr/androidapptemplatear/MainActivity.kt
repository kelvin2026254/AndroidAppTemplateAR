
package com.ifpr.androidapptemplatear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ifpr.androidapptemplatear.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioManagerGame: AudioManagerGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa a música ambiente do jogo
        audioManagerGame = AudioManagerGame(this)
        audioManagerGame.startMusic()

        // Configura a navegação inferior
        val navView: BottomNavigationView = binding.navView

        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

        if (::audioManagerGame.isInitialized) {
            audioManagerGame.startMusic()
        }
    }

    override fun onPause() {
        if (::audioManagerGame.isInitialized) {
            audioManagerGame.pauseMusic()
        }

        super.onPause()
    }

    override fun onDestroy() {
        if (::audioManagerGame.isInitialized) {
            audioManagerGame.release()
        }

        super.onDestroy()
    }
}
