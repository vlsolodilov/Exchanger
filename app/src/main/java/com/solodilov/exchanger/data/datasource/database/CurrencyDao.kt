package com.solodilov.exchanger.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.solodilov.exchanger.data.datasource.database.entity.CurrencyDb

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(currencies: List<CurrencyDb>)

    @Query("SELECT * FROM currency")
    fun getCurrencies(): List<CurrencyDb>

    @Query("UPDATE currency SET amount = :newAmount WHERE name = :name")
    suspend fun updateAmountByName(name: String, newAmount: Float)

    @Transaction
    suspend fun updateAmount(from: String, fromAmount: Float, to: String, toAmount: Float) {
        try {
            updateAmountByName(from, fromAmount)
            updateAmountByName(to, toAmount)
        } catch (e: Exception) {

        }
    }
}