package ru.kheynov.todoappyandex.featureSettings.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.kheynov.todoappyandex.di.ViewModelKey
import ru.kheynov.todoappyandex.featureSettings.presentation.SettingsViewModel

@Module
interface SettingsModule {
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun bindViewModel(viewModel: SettingsViewModel): ViewModel
}