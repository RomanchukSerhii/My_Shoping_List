package com.example.myshopinglist.presentation.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.databinding.ItemShopDisabledBinding
import com.example.myshopinglist.databinding.ItemShopEnabledBinding
import com.example.myshopinglist.domain.ShopItem

class ShopItemViewHolder(
    private val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(shopItem: ShopItem) {
        when (binding) {
            is ItemShopDisabledBinding -> {
                binding.tvName.text = shopItem.name
                binding.tvCount.text = shopItem.count.toString()
            }
            is ItemShopEnabledBinding -> {
                binding.tvName.text = shopItem.name
                binding.tvCount.text = shopItem.count.toString()
            }
        }
    }
}