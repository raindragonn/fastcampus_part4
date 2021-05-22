package com.raindragonn.chapter02_music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.raindragonn.chapter02_music.databinding.FragmentPlayerBinding
import com.raindragonn.chapter02_music.network.NetworkManager

// Created by raindragonn on 2021/05/21.

class PlayerFragment : Fragment(R.layout.fragment_player) {
    companion object {
        fun newInstance(): PlayerFragment = PlayerFragment()
    }

    private val TAG: String = PlayerFragment::class.java.simpleName
    private val adapter by lazy {
        PlayListAdapter {
            playMusic(it)
        }
    }

    private var model = PlayerModel()
    private var binding: FragmentPlayerBinding? = null
    private var player: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayer()
        initViews()
        getVideoList()
    }

    private fun initPlayer() {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        binding?.apply {
            playerView.player = player
            player?.addListener(object : Player.EventListener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        ivControlPlay.setImageResource(R.drawable.ic_pause)
                    } else {
                        ivControlPlay.setImageResource(R.drawable.ic_play)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition = newIndex.toInt()
                    
                    adapter.submitList(model.getAdapterModels())
                }
            })
        }
    }

    private fun initViews() {
        binding?.apply {
            ivControlList.setOnClickListener {
                //todo 만약에 서버에서 데이터가 다 불려오지 않은 상태 일 때
                if (model.currentPosition == -1) return@setOnClickListener

                groupPlayer.isVisible = model.isWatchingListView
                groupList.isVisible = model.isWatchingListView.not()
                model.isWatchingListView = !model.isWatchingListView
            }

            rvPlaylist.adapter = adapter
            rvPlaylist.layoutManager = LinearLayoutManager(context)

            ivControlPlay.setOnClickListener {
                onControlPlay()
            }

            ivControlNext.setOnClickListener {
                onControlNext()
            }

            ivControlPrev.setOnClickListener {
                onControlPrev()
            }
        }
    }

    private fun onControlPrev() {
        val prevMusic = model.getPrevMusic() ?: return
        playMusic(prevMusic)
    }

    private fun onControlNext() {
        val nextMusic = model.getNextMusic() ?: return
        playMusic(nextMusic)
    }

    private fun onControlPlay() {
        val player = this@PlayerFragment.player ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    private fun getVideoList() {
        NetworkManager.getList(
            response = { call, response ->
                response.body()?.let { musicDto ->
                    model = musicDto.mapper()
                    setMusicList(model.getAdapterModels())
                    adapter.submitList(model.getAdapterModels())
                }
            }, failure = { call, t ->

            })
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        context?.let { context ->
            player?.addMediaItems(modelList.map { model ->
                // media item 에 아이디 및 태그 지정 가능
                MediaItem.Builder()
                    .setMediaId(model.id.toString())
                    .setUri(model.streamUrl)
                    .build()
            })
            player?.prepare()
        }
    }

    private fun playMusic(musicModel: MusicModel) {
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}