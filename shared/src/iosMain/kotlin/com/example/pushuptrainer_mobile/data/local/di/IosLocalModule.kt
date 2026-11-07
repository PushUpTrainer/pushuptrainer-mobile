package com.example.pushuptrainer_mobile.data.local.di

import com.example.pushuptrainer_mobile.data.local.database.di.iosDatabaseModule
import org.koin.dsl.module

val iosLocalModule = module {
    includes(iosDatabaseModule)
}
