package com.example.myshopinglist.di.components

import com.example.myshopinglist.di.modules.ViewModelModule
import dagger.Component

@Component(modules = [ViewModelModule::class])
interface ApplicationComponent {
}