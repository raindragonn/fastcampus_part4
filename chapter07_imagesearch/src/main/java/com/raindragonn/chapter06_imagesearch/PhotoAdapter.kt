package com.raindragonn.chapter06_imagesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.raindragonn.chapter06_imagesearch.data.model.PhotoResponse
import com.raindragonn.chapter06_imagesearch.databinding.ItemPhotoBinding

// Created by raindragonn on 2021/05/27.

class PhotoAdapter(
    private val onClickPhoto: (PhotoResponse) -> Unit
) :
    RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    private var photos: List<PhotoResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    fun setList(nList: List<PhotoResponse>) {
        photos = nList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickPhoto(photos[adapterPosition])
                }
            }
        }

        fun bind(model: PhotoResponse) = with(binding) {
            val dimensionRatio = model.height / model.width.toFloat()
            //스크린의 가로사이즈 - 패딩
            val targetWidth = root.resources.displayMetrics.widthPixels -
                    (root.paddingStart + root.paddingEnd)
            val targetHeight = (targetWidth * dimensionRatio).toInt()

            clContentsContainer.layoutParams =
                clContentsContainer.layoutParams.apply {
                    height = targetHeight
                }

            Glide.with(root)
                .load(model.urls?.regular)
                .thumbnail(
                    Glide.with(root)
                        .load(model.urls?.thumb)
                        .transition(DrawableTransitionOptions.withCrossFade())
                )
                .override(targetWidth, targetHeight)
                .into(ivPhotoImage)

            Glide.with(root)
                .load(model.user?.profileImageUrls?.small)
                .placeholder(R.drawable.shpae_profile_placehoder)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProfile)

            if (model.user?.name.isNullOrBlank()) {
                tvAuthor.isVisible = false
            } else {
                tvAuthor.isVisible = true
                tvAuthor.text = model.user?.name

            }

            if (model.description.isNullOrBlank()) {
                tvDescription.isVisible = false
            } else {
                tvDescription.isVisible = true
                tvDescription.text = model.description
            }
        }
    }
}
