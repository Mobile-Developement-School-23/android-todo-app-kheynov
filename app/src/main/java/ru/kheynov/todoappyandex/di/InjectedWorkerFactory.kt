package ru.kheynov.todoappyandex.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.kheynov.todoappyandex.core.workers.SyncTodosWorker
import javax.inject.Inject

class InjectedWorkerFactory @Inject constructor(
    private val syncTodoWorkerFactory: SyncTodosWorker.Factory,
) : WorkerFactory() {
    
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncTodosWorker::class.java.name ->
                syncTodoWorkerFactory.create(appContext, workerParameters)
            
            else -> null
        }
    }
}