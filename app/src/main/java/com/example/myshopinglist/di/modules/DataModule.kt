package com.example.myshopinglist.di.modules

import android.app.Application
import com.example.myshopinglist.data.AppDataBase
import com.example.myshopinglist.data.ShopListDao
import com.example.myshopinglist.data.ShopListRepositoryImpl
import com.example.myshopinglist.di.annotations.ApplicationScope
import com.example.myshopinglist.domain.ShopListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindShopListRepository(impl: ShopListRepositoryImpl): ShopListRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideShopListDao(application: Application): ShopListDao {
            return AppDataBase.getInstance(application).shopListDao()
        }
    }
}