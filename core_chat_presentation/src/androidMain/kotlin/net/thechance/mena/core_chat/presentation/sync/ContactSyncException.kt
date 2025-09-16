package net.thechance.mena.core_chat.presentation.sync

enum class ContactSyncException(val code: Int) {
    PERMISSION_DENIED_EXCEPTION(1),
    NETWORK_EXCEPTION(2),
    UNKNOWN_EXCEPTION(3);

    companion object {
        fun fromCode(code: Int): ContactSyncException = when (code) {
            1 -> PERMISSION_DENIED_EXCEPTION
            2 -> NETWORK_EXCEPTION
            else -> UNKNOWN_EXCEPTION
        }
    }
}