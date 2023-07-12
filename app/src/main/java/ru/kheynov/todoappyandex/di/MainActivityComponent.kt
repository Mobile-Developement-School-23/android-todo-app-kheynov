package ru.kheynov.todoappyandex.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.MainActivity

@Subcomponent
@ActivityScope
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }

    fun inject(activity: MainActivity)
}