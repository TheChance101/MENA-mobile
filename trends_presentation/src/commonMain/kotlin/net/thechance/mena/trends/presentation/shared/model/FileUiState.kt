package net.thechance.mena.trends.presentation.shared.model

data class FileUiState(
    val id: String = "",
    val name: String = "",
    val extension: String = "",
    val sizeInBytes: Long = 0L,
    val sizeInMegaBytes: String = "",
    val bytes: ByteArray = ByteArray(0),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileUiState

        if (sizeInBytes != other.sizeInBytes) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (extension != other.extension) return false
        if (sizeInMegaBytes != other.sizeInMegaBytes) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sizeInBytes.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + sizeInMegaBytes.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}