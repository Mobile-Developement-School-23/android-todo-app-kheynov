package ru.kheynov.todoappyandex.core.data.mappers

import ru.kheynov.todoappyandex.core.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Mapper from local DTO to domain entity
 */
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

/**
 * Mapper from domain entity to local DTO
 */
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
