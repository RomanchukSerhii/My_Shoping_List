package com.example.myshopinglist.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.myshopinglist.R
import com.example.myshopinglist.domain.ShopItem

class ShopListAdapter : ListAdapter<ShopItem, ShopItemViewHolder>(DiffCallback) {

    var onShopItemLongClickListener: ((shopItem: ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((shopItem: ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val layout = when (viewType) {
            ENABLED_TYPE -> R.layout.item_shop_enabled
            DISABLED_TYPE -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val shopItemView = LayoutInflater.from(parent.context).inflate(
            layout,
            parent,
            false
        )
        return ShopItemViewHolder(shopItemView)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val currentShopItem = getItem(position)
        setListeners(holder.itemView, currentShopItem)
        holder.bind(currentShopItem)
    }

    override fun getItemViewType(position: Int): Int {
        val currentShopItem = getItem(position)
        return if (currentShopItem.enabled) ENABLED_TYPE else DISABLED_TYPE
    }

    private fun setListeners(shopItemView: View, shopItem: ShopItem) {
        shopItemView.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }

        shopItemView.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
    }

    companion object {
        const val ENABLED_TYPE = 0
        const val DISABLED_TYPE = 1
        const val MAX_POOL_SIZE = 15

        private val DiffCallback = object : DiffUtil.ItemCallback<ShopItem>() {
            override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}