package com.example.pushuptrainer_mobile.features.home.data.repository.di

import com.example.pushuptrainer_mobile.features.home.data.repository.HomeRepositoryImpl
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import org.koin.dsl.module

internal val homeRepositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
}
