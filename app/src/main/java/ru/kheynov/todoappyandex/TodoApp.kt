package ru.kheynov.todoappyandex

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import ru.kheynov.todoappyandex.di.AppComponent
import ru.kheynov.todoappyandex.di.DaggerAppComponent
import ru.kheynov.todoappyandex.di.InjectedWorkerFactory
import javax.inject.Inject

class TodoApp : Application() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var workerFactory: InjectedWorkerFactory

    override fun onCreate() {
        appComponent = DaggerAppComponent.factory().create(
            this,
            applicationContext
        )
        appComponent.inject(this)

        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "channel_id",
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        applicationContext.getString(R.string.notification_channel_description)
                }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)
    }
}

// allows to take appComponent from any place with only context
val Context.appComponent: AppComponent
    get() = when (this) {
        is TodoApp -> appComponent
        else -> (this.applicationContext as TodoApp).appComponent
    }
