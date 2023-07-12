package ru.kheynov.todoappyandex.core.data.mappers

import ru.kheynov.todoappyandex.core.data.model.remote.TodoRemoteDTO
import ru.kheynov.todoappyandex.core.data.util.Converters
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Mapper from remote DTO to domain entity
 */
fun TodoRemoteDTO.toDomain(): TodoItem =
    TodoItem(
        id = id,
        text = text,
        urgency = Converters().toUrgency(importance),
        deadline = deadline?.let { LocalDate.ofEpochDay(it) },
        isDone = done,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(createdAt),
            ZoneId.systemDefault().rules.getOffset(Instant.now())
        ),
        editedAt = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(updatedAt),
            ZoneId.systemDefault().rules.getOffset(Instant.now())
        ),
        color = color
    )

/**
 * Mapper from domain entity to remote DTO
 */
fun TodoItem.toRemoteDTO(
    deviceId: String,
) = TodoRemoteDTO(
    id = id,
    text = text,
    importance = Converters().fromUrgency(urgency),
    deadline = deadline?.toEpochDay(),
    done = isDone,
    createdAt = createdAt.toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now())),
    updatedAt = (
            editedAt
                ?: createdAt
            ).toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now())),
    updatedBy = deviceId,
    color = color
)
