package ru.kheynov.todoappyandex.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.workers.SyncTodosWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    
    override fun onResume() {
        super.onResume()
        
        val syncWorker = PeriodicWorkRequestBuilder<SyncTodosWorker>(
            8L, TimeUnit.HOURS
        ).setConstraints(
            Constraints(
                requiredNetworkType = NetworkType.NOT_ROAMING
            )
        ).build()
        WorkManager.getInstance(this).enqueue(syncWorker)
    }
}
