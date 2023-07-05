package ru.kheynov.todoappyandex.data.mappers

import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun TodoLocalDTO.toDomain() = TodoItem(
    id = id,
    text = text,
    urgency = urgency,
    deadline = deadline?.let { LocalDate.ofEpochDay(it) },
    isDone = isDone,
    createdAt = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(createdAt),
        ZoneId.systemDefault().rules.getOffset(Instant.now())
    ),
    editedAt = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(editedAt),
        ZoneId.systemDefault().rules.getOffset(Instant.now())
    )
)

fun TodoItem.toLocalDTO() = TodoLocalDTO(
    id = id,
    text = text,
    urgency = urgency,
    deadline = deadline?.toEpochDay(),
    isDone = isDone,
    createdAt = createdAt.toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now())),
    editedAt = (
        editedAt
            ?: createdAt
        ).toEpochSecond(ZoneId.systemDefault().rules.getOffset(Instant.now()))
)
