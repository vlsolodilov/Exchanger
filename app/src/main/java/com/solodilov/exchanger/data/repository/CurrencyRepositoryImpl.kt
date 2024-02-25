package com.solodilov.exchanger.data.repository

import com.solodilov.exchanger.data.datasource.database.CurrencyDao
import com.solodilov.exchanger.data.datasource.database.entity.CurrencyDb
import com.solodilov.exchanger.data.datasource.database.entity.toCurrency
import com.solodilov.exchanger.data.datasource.network.CbrApi
import com.solodilov.exchanger.domain.entity.Currency
import com.solodilov.exchanger.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CbrApi,
    private val dao: CurrencyDao,
): CurrencyRepository {

    override suspend fun getCurrencies(forceRefresh: Boolean): List<Currency> {
        return if (forceRefresh) {
            refreshAndCache()
        } else {
            val currenciesDb = dao.getCurrencies().also { currenciesDb ->
                if (currenciesDb.isEmpty()) {
                    return refreshAndCache()
                }
            }
            currenciesDb.map { it.toCurrency() }
        }
    }

    override suspend fun updateAmountByNames(
        from: String,
        fromAmount: Float,
        to: String,
        toAmount: Float,
    ) {
        dao.updateAmount(from, fromAmount, to, toAmount)
    }

    private suspend fun refreshAndCache(): List<Currency> {
        val oldCurrencies = dao.getCurrencies()
        val newCurrencies = api.getCurrencyList().rates
        val currencies = if (oldCurrencies.isEmpty() ) {
            newCurrencies.map { (name, rate) ->
                CurrencyDb(
                    name = name,
                    rate = rate,
                    amount = 100f,
                )
            }
        } else {
            oldCurrencies.map { oldCurrency ->
                newCurrencies[oldCurrency.name]?.let { newRate ->
                    oldCurrency.copy(rate = newRate)
                } ?: oldCurrency
            }
        }
        dao.insertCurrencies(currencies)
        return currencies.map { it.toCurrency() }
    }
}