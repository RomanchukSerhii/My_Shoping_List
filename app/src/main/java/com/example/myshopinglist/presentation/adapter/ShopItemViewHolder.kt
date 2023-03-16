package com.example.myshopinglist.presentation.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.domain.ShopItem

class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(shopItem: ShopItem) {
        val tvName = itemView.findViewById<TextView>(R.id.tv_name)
        val tvCount = itemView.findViewById<TextView>(R.id.tv_count)
        tvName.text = shopItem.name
        tvCount.text = shopItem.count.toString()
    }
}