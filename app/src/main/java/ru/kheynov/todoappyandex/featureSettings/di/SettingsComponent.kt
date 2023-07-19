package ru.kheynov.todoappyandex.featureSettings.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.di.ViewModelBuilderModule
import ru.kheynov.todoappyandex.featureSettings.presentation.SettingsFragment
import javax.inject.Scope

@Scope
annotation class SettingsScope

@Subcomponent(modules = [SettingsModule::class, ViewModelBuilderModule::class])
@SettingsScope
interface SettingsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }

    fun inject(fragment: SettingsFragment)
}