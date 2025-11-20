package net.thechance.mena.identity.domain.useCase.validation.age

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AgeValidator {
    @OptIn(ExperimentalTime::class)
    fun isValid(date: LocalDate): Boolean {
        val today = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault()).date
        val yearAdjustment =
            if (today.month < date.month || (today.month == date.month && today.day <= date.day)) 1 else 0
        val age = today.year - date.year - yearAdjustment

        return age >= MIN_AGE
    }

    companion object {
        private const val MIN_AGE = 14
    }
}