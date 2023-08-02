package com.example.myshopinglist.di.components

import android.app.Application
import com.example.myshopinglist.data.ShopListProvider
import com.example.myshopinglist.di.annotations.ApplicationScope
import com.example.myshopinglist.di.modules.DataModule
import com.example.myshopinglist.di.modules.ViewModelModule
import com.example.myshopinglist.presentation.MainActivity
import com.example.myshopinglist.presentation.ShopItemFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: ShopItemFragment)

    fun inject(provider: ShopListProvider)

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance application: Application
        ) : ApplicationComponent
    }
}