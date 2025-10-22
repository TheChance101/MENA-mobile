package net.thechance.mena.core_chat.domain.event

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a real-time event indicating that messages in a chat have been marked as read.
 *
 * This event is broadcast via WebSocket to all participants in a chat when any user marks
 * messages as read. It enables read receipt tracking and real-time chat synchronization
 * across multiple devices and users.
 *
 * @param readByUserId The unique identifier of the user who marked the messages as read.
 * @param chatId The unique identifier of the chat where messages were marked as read.
 * @param readByMe Indicates whether the current recipient of this event is the one who
 *                    marked the messages as read. `true` if the event recipient is the same
 *                    as [readByUserId], `false` if another user in the chat marked messages as read.
 *                    This helps clients distinguish between their own read actions and others'.
 *
 * @sample
 * ```
 * // When User A marks messages as read in Chat X:
 * // - User A receives: MarkMessageAsReadEvent(readByUserId = A, chatId = X, readByMe = true)
 * // - User B receives: MarkMessageAsReadEvent(readByUserId = A, chatId = X, readByMe = false)
 * // - User C receives: MarkMessageAsReadEvent(readByUserId = A, chatId = X, readByMe = false)
 * ```
 */
@OptIn(ExperimentalUuidApi::class)
data class MarkMessageAsReadEvent(
    val readByUserId: Uuid,
    val chatId: Uuid,
    val readByMe: Boolean
)