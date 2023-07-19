package ru.kheynov.todoappyandex.featureTodosList.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.di.ViewModelBuilderModule
import ru.kheynov.todoappyandex.featureTodosList.presentation.MainScreenFragment
import javax.inject.Scope

@Scope
annotation class TodoListScope

@Subcomponent(modules = [TodoListModule::class, ViewModelBuilderModule::class])
@TodoListScope
interface TodoListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoListComponent
    }

    fun inject(fragment: MainScreenFragment)
}