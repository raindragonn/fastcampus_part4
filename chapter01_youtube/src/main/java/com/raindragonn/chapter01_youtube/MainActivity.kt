package com.raindragonn.chapter01_youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.raindragonn.chapter01_youtube.adapter.VideoAdapter
import com.raindragonn.chapter01_youtube.databinding.ActivityMainBinding
import com.raindragonn.chapter01_youtube.network.NetworkManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG: String = MainActivity::class.java.simpleName
    private val adapter by lazy {
        VideoAdapter { url, title ->
            supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                (it as PlayerFragment).playWithUrl(url, title)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        getList()
    }

    private fun initViews() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_container, PlayerFragment())
            .commit()

        binding.apply {
            rvMain.layoutManager = LinearLayoutManager(this@MainActivity)
            rvMain.adapter = adapter
        }
    }

    fun setMotionProgress(progress: Float) {
        binding.motionMain.progress = progress
    }

    private fun getList() {
        NetworkManager.getVideoList(response = { call, response ->
            if (response.isSuccessful.not()) {
                Log.d(TAG, "dev_log: response fail")
                return@getVideoList
            }
            response.body()?.let {
                adapter.submitList(it.videos)
            }

        }, failure = { call, t ->
            Toast.makeText(this, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }

}