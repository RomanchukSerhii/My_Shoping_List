package com.example.myshopinglist.di.modules

import androidx.lifecycle.ViewModel
import com.example.myshopinglist.di.annotations.ViewModelKey
import com.example.myshopinglist.presentation.MainViewModel
import com.example.myshopinglist.presentation.ShopItemViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopItemViewModel::class)
    fun bindShopItemViewModel(impl: ShopItemViewModel): ViewModel
}