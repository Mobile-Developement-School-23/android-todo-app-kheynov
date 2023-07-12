package ru.kheynov.todoappyandex.featureTodoEditor.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.di.ViewModelBuilderModule
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.TodoFragment
import javax.inject.Scope

@Scope
annotation class TodoEditorScope

@Subcomponent(modules = [TodoEditorModule::class, ViewModelBuilderModule::class])
@TodoEditorScope
interface TodoEditorComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoEditorComponent
    }

    fun inject(fragment: TodoFragment)
}