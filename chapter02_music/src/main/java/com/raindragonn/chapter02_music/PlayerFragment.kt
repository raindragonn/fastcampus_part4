package com.raindragonn.chapter02_music

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.raindragonn.chapter02_music.databinding.FragmentPlayerBinding

// Created by raindragonn on 2021/05/21.

class PlayerFragment : Fragment(R.layout.fragment_player) {
    companion object {
        fun newInstance(): PlayerFragment = PlayerFragment()
    }

    private var binding: FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayerBinding.bind(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}