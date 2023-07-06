package ru.kheynov.todoappyandex.featureTodoEditor.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.core.di.FragmentScope
import ru.kheynov.todoappyandex.core.di.ViewModelBuilderModule
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.TodoFragment

@Subcomponent(modules = [TodoEditorModule::class, ViewModelBuilderModule::class])
@FragmentScope
interface TodoEditorComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoEditorComponent
    }
    
    fun inject(fragment: TodoFragment)
}