package com.solodilov.exchanger.domain.usecase

import com.solodilov.exchanger.domain.entity.Currency
import com.solodilov.exchanger.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(private val currencyRepository: CurrencyRepository) {

    suspend operator fun invoke(forceRefresh: Boolean): List<Currency> =
        currencyRepository.getCurrencies(forceRefresh)

}