package com.raindragonn.chapter02_music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        NetworkManager.getList(
            response = { call, response ->
                Log.d(TAG, "dev_log: ${response.body()}")
            }, failure = { call, t ->

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}