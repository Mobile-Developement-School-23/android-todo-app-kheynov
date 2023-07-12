package ru.kheynov.todoappyandex.core.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.core.utils.NetworkException
import ru.kheynov.todoappyandex.core.utils.Resource
import ru.kheynov.todoappyandex.core.utils.ServerSideException

/**
 * Sync todos worker – worker that syncs todos with server
 * @param context context
 * @param params worker params
 * @param repository todo items repository
 */
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

    @AssistedFactory
    interface Factory {
        fun create(
            context: Context,
            params: WorkerParameters,
        ): SyncTodosWorker
    }
}
