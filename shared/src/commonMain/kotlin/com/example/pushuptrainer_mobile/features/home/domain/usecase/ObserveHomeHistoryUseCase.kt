package com.example.pushuptrainer_mobile.features.home.domain.usecase

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

internal class ObserveHomeHistoryUseCase(private val repository: HomeRepository) {
    operator fun invoke(selectedDate: LocalDate? = null): Flow<HomeHistory> =
        repository.observeHistory(selectedDate)
}
