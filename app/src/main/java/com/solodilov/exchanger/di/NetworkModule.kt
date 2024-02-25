package com.solodilov.exchanger.di

import com.solodilov.exchanger.data.datasource.network.CbrApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

	private companion object {

		const val BASE_URL = "https://www.cbr-xml-daily.ru/"
	}

	@Provides
	fun provideGsonConverterFactory(): GsonConverterFactory =
		GsonConverterFactory.create()

	@Singleton
	@Provides
	fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
		.addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
		.build()


	@Provides
	@Singleton
	fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
		okHttpClient: OkHttpClient,
	): Retrofit =
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(gsonConverterFactory)
			.client(okHttpClient)
			.build()

	@Provides
	@Singleton
	fun provideCbrApi(
		retrofit: Retrofit,
	): CbrApi =
		retrofit.create(CbrApi::class.java)

}