package com.solodilov.exchanger.data.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solodilov.exchanger.data.datasource.database.entity.CurrencyDb

@Database(
    entities = [CurrencyDb::class],
    version = 1,
    exportSchema = false
)
abstract class ExchangerDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    companion object {

        @Volatile
        private var INSTANCE: ExchangerDatabase? = null

        fun getInstance(context: Context): ExchangerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ExchangerDatabase::class.java, "Exchanger.db"
            ).build()
    }
}
