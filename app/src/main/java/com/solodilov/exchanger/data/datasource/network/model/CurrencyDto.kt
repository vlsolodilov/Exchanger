package com.solodilov.exchanger.data.datasource.network.model


import com.google.gson.annotations.SerializedName

data class CurrencyDto(
    @SerializedName("disclaimer")
    val disclaimer: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: Map<String, Float>
)