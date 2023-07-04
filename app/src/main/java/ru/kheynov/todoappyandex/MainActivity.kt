package ru.kheynov.todoappyandex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.todoappyandex.core.utils.NetworkListener
import ru.kheynov.todoappyandex.core.workers.SyncTodosWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val PERIODIC_SYNC_INTERVAL = 8L

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
            PERIODIC_SYNC_INTERVAL,
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


