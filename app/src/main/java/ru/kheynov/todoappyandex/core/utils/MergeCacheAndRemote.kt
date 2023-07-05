package ru.kheynov.todoappyandex.core.utils

import ru.kheynov.todoappyandex.core.domain.entities.TodoItem

enum class Operation {
    ADD, DELETE, UPDATE
}

class Item(
    val todo: TodoItem,
    val operation: Operation,
)

/**
 * Merges local and remote todos
 * @param local [List] of [TodoItem] from local database
 * @param remote [List] of [TodoItem] from remote database
 * @return [List] of [Item] to be synced
 */
fun mergeCacheAndRemote(
    local: List<TodoItem>,
    remote: List<TodoItem>,
): List<Item> {
    val localItems = local.map { it.id }.toHashSet()
    val remoteItems = remote.map { it.id }.toHashSet()
    
    val toAddIds = remoteItems.subtract(localItems)
    val toDeleteIds = localItems.subtract(remoteItems)
    val toUpdateIds = localItems.intersect(remoteItems)
    
    val result = mutableListOf<Item>()
    result.addAll(local.filter { it.id in toDeleteIds }.map { Item(it, Operation.DELETE) })
    result.addAll(remote.filter { it.id in toAddIds }.map { Item(it, Operation.ADD) })
    local.forEach {
        if (it.id in toUpdateIds) {
            val remoteItem = remote.first { remoteItem -> remoteItem.id == it.id }
            result.add(
                if (
                    (remoteItem.editedAt ?: remoteItem.createdAt) >
                    (it.editedAt ?: it.createdAt)
                )
                    Item(remoteItem, Operation.UPDATE)
                else
                    Item(it, Operation.UPDATE)
            )
        }
    }
    
    return result
}
