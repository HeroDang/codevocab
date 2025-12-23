package com.group20.codevocab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ItemBannerSlideBinding

data class BannerItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val backgroundRes: Int
)

class BannerAdapter(private val items: List<BannerItem>) :
    RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBannerSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BannerItem) {
            binding.tvBannerTitle.text = item.title
            binding.tvBannerSubtitle.text = item.subtitle
            binding.ivBannerIcon.setImageResource(item.iconRes)
            binding.layoutBanner.setBackgroundResource(item.backgroundRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerSlideBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
