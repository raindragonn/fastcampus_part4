package com.raindragonn.chapter05_githubrepo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raindragonn.chapter05_githubrepo.databinding.ItemRepositoryBinding
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity
import com.raindragonn.chapter05_githubrepo.util.loadCenterInside

// Created by raindragonn on 2021/05/26.

class RepositoryAdapter(private val itemClickListener: (GithubRepoEntity) -> Unit) :
    ListAdapter<GithubRepoEntity, RepositoryAdapter.ViewHolder>(differ) {
    companion object {
        val differ = object : DiffUtil.ItemCallback<GithubRepoEntity>() {
            override fun areItemsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean =
                oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemRepositoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener(currentList[adapterPosition])
                }
            }
        }

        fun bind(model: GithubRepoEntity) = with(binding) {
            ivProfile.loadCenterInside(model.owner.avatarUrl, 24f)
            tvOwner.text = model.owner.login
            tvName.text = model.fullName
            tvSubText.text = model.description
            tvStargazersCount.text = model.stargazersCount.toString()

            model.language?.let {
                tvLanguage.isVisible = true
                tvLanguage.text = it
            } ?: run {
                tvLanguage.isVisible = false
                tvLanguage.text = ""
            }
        }
    }
}