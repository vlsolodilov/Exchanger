package com.solodilov.exchanger.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.solodilov.exchanger.domain.repository.CurrencyRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UpdateDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val currencyRepository: CurrencyRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            currencyRepository.getCurrencies(forceRefresh = true)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val NAME = "UpdateDataWorker"

        fun makeRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<UpdateDataWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.SECONDS,
            ).build()
        }
    }

    class Factory @Inject constructor(
        private val currencyRepository: CurrencyRepository,
    ) : ChildWorkerFactory {

        override fun create(
            context: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return UpdateDataWorker(
                context,
                workerParameters,
                currencyRepository,
            )
        }
    }
}