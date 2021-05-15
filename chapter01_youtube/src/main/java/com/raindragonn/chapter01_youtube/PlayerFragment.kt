package com.raindragonn.chapter01_youtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.raindragonn.chapter01_youtube.adapter.VideoAdapter
import com.raindragonn.chapter01_youtube.databinding.FragmentPlayerBinding
import com.raindragonn.chapter01_youtube.network.NetworkManager
import kotlin.math.abs

// Created by raindragonn on 2021/05/15.

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private lateinit var binding: FragmentPlayerBinding
    private val adapter by lazy {
        VideoAdapter { url, title ->
            playWithUrl(url, title)
        }
    }
    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (::binding.isInitialized.not())
            binding = FragmentPlayerBinding.bind(view)

        initMotionLayoutEvent()
        initRecyclerView()
        initControlBtn()
        initPlayer()
        getList()
    }

    private fun initMotionLayoutEvent() {
        binding.motionPlayer.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding.let {
                    (activity as? MainActivity)?.also { mainActivity ->
                        mainActivity.setMotionProgress(abs(progress))
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

    }

    private fun initRecyclerView() {
        binding.apply {
            rvPlay.layoutManager = LinearLayoutManager(requireContext())
            rvPlay.adapter = adapter
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
        player?.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                when (isPlaying) {
                    true -> {
                        binding.ivBottomPlay.setImageResource(R.drawable.ic_pause)
                    }
                    false -> {
                        binding.ivBottomPlay.setImageResource(R.drawable.ic_play)
                    }
                }
            }
        })
    }

    private fun initControlBtn() {
        binding.apply {
            ivBottomPlay.setOnClickListener {
                val player = player ?: return@setOnClickListener
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
        }
    }

    private fun getList() {
        NetworkManager.getVideoList(response = { call, response ->
            if (response.isSuccessful.not()) {
                return@getVideoList
            }
            response.body()?.let {
                adapter.submitList(it.videos)
            }

        }, failure = { call, t ->
            Toast.makeText(requireContext(), "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }

    fun playWithUrl(url: String, title: String) {
        play(url)

        binding.apply {
            motionPlayer.transitionToEnd()
            tvBottomTitle.text = title
        }
    }

    private fun play(url: String) {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}