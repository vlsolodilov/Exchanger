package com.solodilov.exchanger.di

import com.solodilov.exchanger.data.workers.ChildWorkerFactory
import com.solodilov.exchanger.data.workers.UpdateDataWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(UpdateDataWorker::class)
    fun bindUpdateDataWorker(worker: UpdateDataWorker.Factory): ChildWorkerFactory
}