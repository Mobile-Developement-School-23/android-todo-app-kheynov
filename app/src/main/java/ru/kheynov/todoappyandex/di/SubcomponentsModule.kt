package ru.kheynov.todoappyandex.di

import dagger.Module
import ru.kheynov.todoappyandex.featureTodoEditor.di.TodoEditorComponent
import ru.kheynov.todoappyandex.featureTodosList.di.TodoListComponent

@Module(
    subcomponents = [
        TodoListComponent::class,
        MainActivityComponent::class,
        TodoEditorComponent::class,
    ]
)
interface SubcomponentsModule