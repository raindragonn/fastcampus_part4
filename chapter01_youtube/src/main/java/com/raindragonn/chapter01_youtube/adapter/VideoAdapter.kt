package com.raindragonn.chapter01_youtube.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raindragonn.chapter01_youtube.databinding.ItemVideoBinding
import com.raindragonn.chapter01_youtube.model.Video

// Created by raindragonn on 2021/05/15.

class VideoAdapter(val itemClickListener: (String,String) -> Unit) :
    ListAdapter<Video, VideoAdapter.ViewHolder>(differ) {
    companion object {
        val differ = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val model = currentList[adapterPosition]
                itemClickListener(model.sources,model.title)
            }
        }

        fun bind(model: Video) {
            binding.apply {
                tvTitle.text = model.title
                tvSubTitle.text = model.subtitle
                Glide.with(ivThumb).load(model.thumb).into(ivThumb)
            }
        }
    }
}