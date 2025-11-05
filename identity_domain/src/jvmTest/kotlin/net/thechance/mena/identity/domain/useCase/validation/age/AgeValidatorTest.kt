package net.thechance.mena.identity.domain.useCase.validation.age

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AgeValidatorTest {
    private val ageValidator = AgeValidator()

    @Test
    fun `should return true when age is valid`() {
        val date = LocalDate(2000, 1, 1)

        val result = ageValidator.isValid(date)

        assert(result)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `should return false when age is invalid`() {
        val date = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        val result = ageValidator.isValid(date)

        assert(!result)
    }
}