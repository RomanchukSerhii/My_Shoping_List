package com.example.myshopinglist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.domain.ShopItem

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    var shopList = listOf<ShopItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        val currentShopItem = shopList[position]
        setListeners(holder.itemView, currentShopItem)
        holder.bind(currentShopItem)
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentShopItem = shopList[position]
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

    class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(shopItem: ShopItem) {
            val tvName = itemView.findViewById<TextView>(R.id.tv_name)
            val tvCount = itemView.findViewById<TextView>(R.id.tv_count)
            tvName.text = shopItem.name
            tvCount.text = shopItem.count.toString()
        }
    }

    companion object {
        const val ENABLED_TYPE = 0
        const val DISABLED_TYPE = 1
        const val MAX_POOL_SIZE = 15
    }
}