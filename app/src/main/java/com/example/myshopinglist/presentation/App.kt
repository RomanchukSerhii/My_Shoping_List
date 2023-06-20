package com.example.myshopinglist.presentation

import android.app.Application
import com.example.myshopinglist.di.components.DaggerApplicationComponent

class App : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}