package com.solodilov.exchanger.domain.entity

data class Currency(
    val name: String,
    val rate: Float,
    val amount: Float,
)