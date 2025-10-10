package net.thechance.mena.trends.domain.validation

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import net.thechance.mena.trends.domain.exception.MaxFileDurationExceededException
import net.thechance.mena.trends.domain.exception.MaxFileSizeExceededException
import kotlin.test.Test

class VideoValidatorTest {
    private val validator = VideoValidator()

    @Test
    fun `validateSize should not throw exception when size is less than Max size allowed`() {
        val result = validator.validateSize(ALLOWED_FILE_SIZE)

        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `validateSize should not throw exception when size is equal to allowed size`() {
        val result = validator.validateSize(MAX_FILE_SIZE_100_MB)

        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `validateSize should throw MaxFileSizeExceededException when size is too large`() {
        assertFailure {
            validator.validateSize(TOO_LARGE_FILE_SIZE_200_MB)
        }.isInstanceOf(MaxFileSizeExceededException::class)
    }

    @Test
    fun `validateDuration should not throw exception when duration is less than Max duration allowed`() {
        val result = validator.validateDuration(ALLOWED_FILE_DURATION_IN_MILLIS)

        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `validateDuration should not throw exception when duration is equal to allowed duration`() {
        val result = validator.validateDuration(MAX_LARGE_DURATION_60_SECONDS)

        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `validateDuration should throw MaxFileDurationExceededException when duration is too large`() {
        assertFailure {
            validator.validateDuration(TOO_LARGE_DURATION_70_SECONDS)
        }.isInstanceOf(MaxFileDurationExceededException::class)
    }

    @Test
    fun `validateDuration should throw MaxFileDurationExceededException when duration is null`() {
        assertFailure {
            validator.validateDuration(null)
        }.isInstanceOf(MaxFileDurationExceededException::class)
    }

    private companion object {
        const val ALLOWED_FILE_SIZE = 50 * 1024 * 1024L
        const val MAX_FILE_SIZE_100_MB = 100 * 1024 * 1024L
        const val TOO_LARGE_FILE_SIZE_200_MB = 200 * 1024 * 1024L

        const val ALLOWED_FILE_DURATION_IN_MILLIS = 30_000L
        const val MAX_LARGE_DURATION_60_SECONDS = 60_000L
        const val TOO_LARGE_DURATION_70_SECONDS = 70_000L
    }
}