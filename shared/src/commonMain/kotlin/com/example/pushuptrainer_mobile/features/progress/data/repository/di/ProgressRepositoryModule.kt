package com.example.pushuptrainer_mobile.features.progress.data.repository.di

import com.example.pushuptrainer_mobile.features.progress.data.repository.ProgressRepositoryImpl
import com.example.pushuptrainer_mobile.features.progress.domain.repository.ProgressRepository
import org.koin.dsl.module

internal val progressRepositoryModule = module {
    single<ProgressRepository> { ProgressRepositoryImpl(get()) }
}
