package ru.kheynov.todoappyandex.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.NetworkListener
import ru.kheynov.todoappyandex.workers.SyncTodosWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        networkListener.start()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val syncWorker = PeriodicWorkRequestBuilder<SyncTodosWorker>(
            8L,
            TimeUnit.HOURS
        ).setConstraints(
            Constraints(
                requiredNetworkType = NetworkType.NOT_ROAMING
            )
        ).build()
        WorkManager.getInstance(this).enqueue(syncWorker)
    }

    override fun onStop() {
        val syncWorker = OneTimeWorkRequestBuilder<SyncTodosWorker>()
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.NOT_ROAMING
                )
            ).addTag("INTERNET_LOST_N_FOUND")
            .build()
        WorkManager.getInstance(this).enqueue(syncWorker)
        networkListener.stop()
        super.onStop()
    }
}
