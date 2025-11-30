package net.thechance.mena.trends.presentation.shared.base

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map

sealed interface PagingEvents<T> {
    data class Update<T>(
        val itemId: String,
        val transform: (T) -> T
    ): PagingEvents<T>

    data class Remove<T>(
        val itemId: String,
        val action: (T) -> Unit = {}
    ): PagingEvents<T>
}

fun <T : Any> PagingData<T>.applyEvent(
    event: PagingEvents<T>,
    getItemId: (T) -> String
): PagingData<T> {
    return when(event) {
        is PagingEvents.Update<T> -> {
            this.map { item ->
                if(getItemId(item) == event.itemId) event.transform(item) else item
            }
        }
        is PagingEvents.Remove<T> -> {
            this.filter { item ->
                if(getItemId(item) == event.itemId) {
                    event.action(item)
                    false
                } else true
            }
        }
    }
}