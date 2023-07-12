package ru.kheynov.todoappyandex.featureTodoEditor.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.kheynov.todoappyandex.di.ViewModelKey
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.TodoViewModel

@Module
interface TodoEditorModule {
    @Binds
    @IntoMap
    @ViewModelKey(TodoViewModel::class)
    fun bindViewModel(viewmodel: TodoViewModel): ViewModel
}