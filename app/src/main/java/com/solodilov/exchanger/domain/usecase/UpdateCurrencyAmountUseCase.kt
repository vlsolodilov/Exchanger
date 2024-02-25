package com.solodilov.exchanger.domain.usecase

import com.solodilov.exchanger.domain.repository.CurrencyRepository
import javax.inject.Inject

class UpdateCurrencyAmountUseCase @Inject constructor(private val currencyRepository: CurrencyRepository) {

    suspend operator fun invoke(from: String, fromAmount: Float, to: String, toAmount: Float) =
        currencyRepository.updateAmountByNames(from, fromAmount, to, toAmount)

}