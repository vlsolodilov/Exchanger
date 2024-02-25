package com.solodilov.exchanger

import android.app.Application
import androidx.work.Configuration
import com.solodilov.exchanger.data.workers.ExchangerWorkerFactory
import com.solodilov.exchanger.di.DaggerApplicationComponent
import javax.inject.Inject

class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: ExchangerWorkerFactory

    val appComponent = DaggerApplicationComponent.factory().create(this)

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

}