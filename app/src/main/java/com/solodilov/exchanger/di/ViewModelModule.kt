package com.solodilov.exchanger.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.solodilov.exchanger.presentation.common.ViewModelFactory
import com.solodilov.exchanger.presentation.converter.ConverterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ConverterViewModel::class)
    fun bindConverterViewModel(viewModel: ConverterViewModel): ViewModel

}