package com.raindragonn.chapter03_map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.raindragonn.chapter03_map.databinding.ItemSearchResultBinding
import com.raindragonn.chapter03_map.model.SearchResultEntity

// Created by raindragonn on 2021/05/23.

class SearchRecyclerAdapter :
    RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {

    private var searchResultClickListener: ((SearchResultEntity) -> Unit)? = null
    private var searchResultList: List<SearchResultEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(ItemSearchResultBinding.bind(view))
    }

    fun setSearchResultListener(nListener: (SearchResultEntity) -> Unit) {
        searchResultClickListener = nListener
    }

    fun setList(nList: List<SearchResultEntity>) {
        searchResultList = nList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(searchResultList[position])
    }
    override fun getItemCount(): Int = searchResultList.size

    inner class ViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    searchResultClickListener?.invoke(searchResultList[adapterPosition])
                }
            }
        }

        fun bind(data: SearchResultEntity) = with(binding) {
            tvTitle.text = data.fullAddress
            tvSubTitle.text = data.name
        }
    }
}