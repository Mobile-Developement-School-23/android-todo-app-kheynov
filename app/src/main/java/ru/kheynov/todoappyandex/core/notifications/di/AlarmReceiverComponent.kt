package ru.kheynov.todoappyandex.core.notifications.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.core.notifications.AlarmReceiver


@Subcomponent
interface AlarmReceiverComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmReceiverComponent
    }

    fun inject(receiver: AlarmReceiver)
}