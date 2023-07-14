package ru.kheynov.todoappyandex.core.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.appComponent
import ru.kheynov.todoappyandex.core.data.local.TodoLocalDAO
import ru.kheynov.todoappyandex.core.data.mappers.toDomain
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

private const val SECONDS_IN_DAY: Long = 24 * 60 * 60

class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var todoDao: TodoLocalDAO

    override fun onReceive(context: Context?, intent: Intent?) {
        (context ?: return)
            .appComponent
            .alarmReceiverComponent()
            .create()
            .inject(this)
        CoroutineScope(Dispatchers.IO).launch {
            val todos = todoDao.getTodos()
                .filter {
                    val todoDate = it.deadline?.let { deadline ->
                        Instant.ofEpochSecond(deadline * SECONDS_IN_DAY)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    todoDate == LocalDate.now() && !it.isDone
                }.map { it.toDomain() }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            todos.forEach { todo ->
                val todoUrgency = when (todo.urgency) {
                    TodoUrgency.LOW -> context.getString(R.string.low_urgency)
                    TodoUrgency.STANDARD -> context.getString(R.string.standard_urgency)
                    TodoUrgency.HIGH -> context.getString(R.string.high_urgency)
                }
                val notification = NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.deadline_notification_title))
                    .setContentText(
                        context.resources.getString(
                            R.string.deadline_notification_description,
                            todo.text,
                            todoUrgency
                        )
                    )
                    .build()
                notificationManager.notify(todo.id.hashCode(), notification)
            }
        }
    }
}
