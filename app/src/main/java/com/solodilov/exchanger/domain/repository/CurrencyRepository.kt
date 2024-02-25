package com.solodilov.exchanger.domain.repository

import com.solodilov.exchanger.domain.entity.Currency

interface CurrencyRepository {
    suspend fun getCurrencies(forceRefresh: Boolean): List<Currency>
    suspend fun updateAmountByNames(from: String, fromAmount: Float, to: String, toAmount: Float)
}