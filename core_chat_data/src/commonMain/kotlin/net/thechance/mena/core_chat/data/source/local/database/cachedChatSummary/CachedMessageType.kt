package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

enum class CachedMessageType {
    TEXT,
    IMAGE,
    AUDIO,
    MONEY,
    AYAH,
    ORDER;

    companion object {
        fun fromString(value: String?): CachedMessageType? {
            return value?.let {
                entries.find { it.name == value }
            }
        }
    }
}