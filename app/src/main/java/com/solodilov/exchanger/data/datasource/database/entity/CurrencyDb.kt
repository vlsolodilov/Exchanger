package com.solodilov.exchanger.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.solodilov.exchanger.domain.entity.Currency

@Entity(tableName = "currency")
data class CurrencyDb(
    @PrimaryKey
    val name: String,
    val rate: Float,
    val amount: Float,
)

fun CurrencyDb.toCurrency() = Currency(
    name = name,
    rate = rate,
    amount = amount,
)