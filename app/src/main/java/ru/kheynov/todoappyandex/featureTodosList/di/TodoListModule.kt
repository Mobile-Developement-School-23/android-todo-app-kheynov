package ru.kheynov.todoappyandex.featureTodosList.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.kheynov.todoappyandex.core.di.ViewModelKey
import ru.kheynov.todoappyandex.featureTodosList.presentation.MainScreenViewModel

@Module
interface TodoListModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainScreenViewModel::class)
    fun bindViewModel(viewmodel: MainScreenViewModel): ViewModel
}