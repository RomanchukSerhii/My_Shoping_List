package com.example.myshopinglist.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myshopinglist.data.ShopListRepositoryImpl
import com.example.myshopinglist.domain.*

class MainViewModel : ViewModel() {
    private val repository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopItemList = MutableLiveData<List<ShopItem>>()

    fun getShopItemList() {
        val shopList = getShopListUseCase.getShopList()
        shopItemList.value = shopList
    }

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
        getShopItemList()
    }

    fun changeEnabledState(shopItem: ShopItem) {
        val changedShopItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(changedShopItem)
        getShopItemList()
    }

}