package com.raindragonn.chapter02_music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.raindragonn.chapter02_music.databinding.FragmentPlayerBinding
import com.raindragonn.chapter02_music.network.NetworkManager

// Created by raindragonn on 2021/05/21.

class PlayerFragment : Fragment(R.layout.fragment_player) {
    companion object {
        fun newInstance(): PlayerFragment = PlayerFragment()
    }

    private val TAG: String = PlayerFragment::class.java.simpleName

    private var binding: FragmentPlayerBinding? = null
    private var isWatchingListView = true

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

        initViews()
        getVideoList()
    }

    private fun initViews() {
        binding?.apply {
            //todo 만약에 서버에서 데이터가 다 불려오지 않은 상태 일 때

            ivControlList.setOnClickListener {
                groupPlayer.isVisible = isWatchingListView
                groupList.isVisible = isWatchingListView.not()
                isWatchingListView = !isWatchingListView
            }
        }
    }

    private fun getVideoList() {
        NetworkManager.getList(
            response = { call, response ->
                response.body()?.let {
                    val modelList = it.musics.mapIndexed { index, musicEntity ->
                        musicEntity.mapper(index.toLong())
                    }

                }
            }, failure = { call, t ->

            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}