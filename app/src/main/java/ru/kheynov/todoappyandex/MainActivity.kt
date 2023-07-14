package ru.kheynov.todoappyandex

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.core.data.local.SettingsStorage
import ru.kheynov.todoappyandex.core.network.NetworkListener
import ru.kheynov.todoappyandex.core.notifications.AlarmReceiver
import ru.kheynov.todoappyandex.core.notifications.AndroidAlarmScheduler
import ru.kheynov.todoappyandex.core.workers.SyncTodosWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val PERIODIC_SYNC_INTERVAL = 8L

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkListener: NetworkListener

    @Inject
    lateinit var settingsStorage: SettingsStorage

    @Inject
    lateinit var alarmScheduler: AndroidAlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.appComponent
            .mainActivityComponent()
            .create()
            .inject(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                settingsStorage.themeFlow.collect { theme ->
                    theme?.let { AppCompatDelegate.setDefaultNightMode(it.value) }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                settingsStorage.notificationsFlow.collect {
                    if (it) {
                        println("alarmScheduled")
                        alarmScheduler.scheduleAlarm()
                    } else {
                        println("alarmCanceled")
                        alarmScheduler.cancelAlarm()
                    }
                }
            }
        }
    }

    override fun onStart() {
        if (Settings.System.canWrite(this)) {
            networkListener.start()
        }
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
        if (Settings.System.canWrite(this)) {
            networkListener.stop()
        }
        super.onStop()
    }
}
