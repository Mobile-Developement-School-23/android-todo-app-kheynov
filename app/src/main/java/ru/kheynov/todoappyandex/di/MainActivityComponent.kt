package ru.kheynov.todoappyandex.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.MainActivity
import javax.inject.Scope

@Scope
annotation class MainActivityScope

@Subcomponent
@MainActivityScope
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }

    fun inject(activity: MainActivity)
}