package com.raindragonn.chapter02_music

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raindragonn.chapter02_music.databinding.ItemMusicBinding

// Created by raindragonn on 2021/05/22.

class PlayListAdapter(private val callBack: (MusicModel) -> Unit) :
    ListAdapter<MusicModel, PlayListAdapter.ViewHolder>(differ) {
    companion object {
        val differ = object : DiffUtil.ItemCallback<MusicModel>() {
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        currentList[position].also { model ->
            holder.bind(model)
        }
    }

    inner class ViewHolder(private val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                callBack(currentList[adapterPosition])
            }
        }

        fun bind(model: MusicModel) {
            binding.apply {
                tvTrack.text = model.track
                tvSinger.text = model.artist

                Glide.with(ivCover).load(model.coverUrl).into(ivCover)

                if (model.isPlaying) {
                    root.setBackgroundColor(Color.GRAY)
                } else {
                    root.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }
}