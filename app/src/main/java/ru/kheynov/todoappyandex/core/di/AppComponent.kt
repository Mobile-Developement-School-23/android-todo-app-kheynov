package ru.kheynov.todoappyandex.core.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.kheynov.todoappyandex.TodoApp
import ru.kheynov.todoappyandex.featureTodoEditor.di.TodoEditorComponent
import ru.kheynov.todoappyandex.featureTodosList.di.TodoListComponent

@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
    ]
)
@AppScope
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application,
            @BindsInstance context: Context,
        ): AppComponent
    }
    
    fun inject(app: TodoApp)
    
    fun todoListComponent(): TodoListComponent.Factory
    fun todoEditorComponent(): TodoEditorComponent.Factory
}