package com.example.pushuptrainer_mobile.features.home.domain.repository

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

internal interface HomeRepository {
    fun observeHistory(selectedDate: LocalDate?): Flow<HomeHistory>
}
