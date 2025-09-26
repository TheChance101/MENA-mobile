package net.thechance.mena.trends.domain.validation

import net.thechance.mena.trends.domain.exception.MaxFileDurationExceededException
import net.thechance.mena.trends.domain.exception.MaxFileSizeExceededException
import org.koin.core.annotation.Single

@Single
class VideoMetaDataValidator {
    fun validateSize(sizeInBytes: Long) {
        sizeInBytes.takeIf { it <= MAX_FILE_SIZE } ?: throw MaxFileSizeExceededException()
    }

    fun validateDuration(durationInMillis: Long?) {
        durationInMillis?.takeIf { it <= MAX_FILE_DURATION } ?: throw MaxFileDurationExceededException()
    }

    private companion object {
        const val MAX_FILE_SIZE = 100 * 1024 * 1024
        const val MAX_FILE_DURATION = 60_000
    }
}