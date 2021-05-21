package com.raindragonn.chapter02_music

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raindragonn.chapter02_music.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.flContainer.id, PlayerFragment.newInstance())
            .commit()
    }
}