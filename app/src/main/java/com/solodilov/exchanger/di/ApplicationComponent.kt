package com.solodilov.exchanger.di

import android.app.Application
import com.solodilov.exchanger.presentation.converter.ConverterFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
	DataModule::class,
	NetworkModule::class,
	ViewModelModule::class,
	WorkerModule::class,
])
interface ApplicationComponent {

	fun inject(fragment: ConverterFragment)


	@Component.Factory
	interface Factory {
		fun create(
			@BindsInstance application: Application,
		): ApplicationComponent
	}
}