package com.solodilov.exchanger.di

import android.app.Application
import com.solodilov.exchanger.data.datasource.database.CurrencyDao
import com.solodilov.exchanger.data.datasource.database.ExchangerDatabase
import com.solodilov.exchanger.data.repository.CurrencyRepositoryImpl
import com.solodilov.exchanger.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DataModule {

	@Singleton
	@Binds
	fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

	companion object {

		@Singleton
		@Provides
		fun provideExchangerDatabase(application: Application): ExchangerDatabase {
			return ExchangerDatabase.getInstance(application)
		}

		@Singleton
		@Provides
		fun provideCurrencyDao(exchangerDatabase: ExchangerDatabase): CurrencyDao {
			return exchangerDatabase.currencyDao()
		}
	}

}