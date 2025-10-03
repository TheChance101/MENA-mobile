package net.thechance.mena.core_chat.data.contacts.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun getUuidOrNull(input: String?): Uuid? {
    return if (input.isNullOrBlank() || input == "null") {
        null
    } else {
        Uuid.parse(input)
    }
}