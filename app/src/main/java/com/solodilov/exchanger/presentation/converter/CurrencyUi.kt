package com.solodilov.exchanger.presentation.converter

data class CurrencyUi(
    val name: String,
    val amount: Float,
    val rate: Float,
    val exchangedName: String,
    val exchangedRate: Float,
)