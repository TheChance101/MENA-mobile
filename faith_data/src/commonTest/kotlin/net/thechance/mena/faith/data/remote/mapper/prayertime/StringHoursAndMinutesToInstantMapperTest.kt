package net.thechance.mena.faith.data.remote.mapper.prayertime

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class StringHoursAndMinutesToInstantMapperTest : StringHoursAndMinutesToInstantMapper {

    @Test
    fun `toInstant should map valid time string to correct Instant`() {
        // Given
        val time = "05:25"
        // When
        val result = time.toInstant(START_OF_DAY_TIMESTAMP)
        // Then
        val expected = Instant.parse("2025-10-10T02:25:00Z") // 10-10-2025 05:25
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toInstant should return startOfDay when time is null`() {
        // When
        val result = null.toInstant(START_OF_DAY_TIMESTAMP)
        // Then
        val expected = Instant.fromEpochMilliseconds(START_OF_DAY_TIMESTAMP)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toInstant should return startOfDay when format invalid`() {
        // Given
        val invalidTime = "abc"
        // When
        val result = invalidTime.toInstant(START_OF_DAY_TIMESTAMP)
        // Then
        val expected = Instant.fromEpochMilliseconds(START_OF_DAY_TIMESTAMP)
        assertThat(result).isEqualTo(expected)
    }

    private companion object {
        const val START_OF_DAY_TIMESTAMP = 1760068800L // 2025-10-10 00:00
    }
}
