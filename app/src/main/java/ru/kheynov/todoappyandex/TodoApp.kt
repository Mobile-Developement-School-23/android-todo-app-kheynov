package ru.kheynov.todoappyandex

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import ru.kheynov.todoappyandex.core.di.AppComponent
import ru.kheynov.todoappyandex.core.di.DaggerAppComponent
import ru.kheynov.todoappyandex.core.di.InjectedWorkerFactory
import javax.inject.Inject

class TodoApp : Application() {
    
    lateinit var appComponent: AppComponent
    
    @Inject
    lateinit var workerFactory: InjectedWorkerFactory
    
    override fun onCreate() {
        appComponent = DaggerAppComponent.factory().create(
            this, applicationContext
        )
        appComponent.inject(this)
        
        super.onCreate()
        
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)
    }
}
