package net.thechance.mena.trends.domain.validation

import net.thechance.mena.trends.domain.exception.MaxFileDurationExceededException
import net.thechance.mena.trends.domain.exception.MaxFileSizeExceededException
import org.koin.core.annotation.Single

@Single
class VideoValidator {
    fun validateSize(sizeInBytes: Long) {
        sizeInBytes.takeIf { it <= MAX_FILE_SIZE_100MB } ?: throw MaxFileSizeExceededException(
            message = "File size exceeds the maximum allowed size of 100MB"
        )
    }

    fun validateDuration(durationInMillis: Long?) {
        durationInMillis?.takeIf {
            it <= MAX_FILE_DURATION_IN_MILLIS
        } ?: throw MaxFileDurationExceededException(
            message = "File duration exceeds the maximum allowed duration of 1 minute"
        )
    }

    private companion object {
        const val MAX_FILE_SIZE_100MB = 100 * 1024 * 1024
        const val MAX_FILE_DURATION_IN_MILLIS = 60_000
    }
}