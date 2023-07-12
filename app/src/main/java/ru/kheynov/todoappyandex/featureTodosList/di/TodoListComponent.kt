package ru.kheynov.todoappyandex.featureTodosList.di

import dagger.Subcomponent
import ru.kheynov.todoappyandex.di.FragmentScope
import ru.kheynov.todoappyandex.di.ViewModelBuilderModule
import ru.kheynov.todoappyandex.featureTodosList.presentation.MainScreenFragment

@Subcomponent(modules = [TodoListModule::class, ViewModelBuilderModule::class])
@FragmentScope
interface TodoListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoListComponent
    }
    
    fun inject(fragment: MainScreenFragment)
}