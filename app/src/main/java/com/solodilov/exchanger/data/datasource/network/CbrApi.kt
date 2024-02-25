package com.solodilov.exchanger.data.datasource.network

import com.solodilov.exchanger.data.datasource.network.model.CurrencyDto
import retrofit2.http.GET

interface CbrApi {

    @GET("latest.js")
    suspend fun getCurrencyList(): CurrencyDto
}