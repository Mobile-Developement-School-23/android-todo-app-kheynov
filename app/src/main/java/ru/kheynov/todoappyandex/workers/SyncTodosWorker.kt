package ru.kheynov.todoappyandex.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kheynov.todoappyandex.core.NetworkException
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.ServerSideException
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository

@HiltWorker
class SyncTodosWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val repository: TodoItemsRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.i("WorkManager", "Starting job")
        when (val res = repository.syncTodos()) {
            is Resource.Failure -> when (res.exception) {
                is NetworkException, is ServerSideException -> Result.retry()
                else -> Result.failure(
                    workDataOf(
                        WorkerKeys.ERROR_MSG to res.exception.localizedMessage
                    )
                )
            }
            
            is Resource.Success -> Result.success()
        }
    }
}
