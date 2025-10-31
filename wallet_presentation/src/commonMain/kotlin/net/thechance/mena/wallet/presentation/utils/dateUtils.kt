@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.wallet.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toLocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.month_april
import mena.wallet_presentation.generated.resources.month_august
import mena.wallet_presentation.generated.resources.month_december
import mena.wallet_presentation.generated.resources.month_february
import mena.wallet_presentation.generated.resources.month_january
import mena.wallet_presentation.generated.resources.month_july
import mena.wallet_presentation.generated.resources.month_june
import mena.wallet_presentation.generated.resources.month_march
import mena.wallet_presentation.generated.resources.month_may
import mena.wallet_presentation.generated.resources.month_november
import mena.wallet_presentation.generated.resources.month_october
import mena.wallet_presentation.generated.resources.month_september
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun getNumberOfDaysInMonth(year: Int, month: Int): Int {
    return YearMonth(year, month).numberOfDays
}

enum class AppMonth(val number: Int, val res: StringResource) {
    January(1, Res.string.month_january),
    February(2, Res.string.month_february),
    March(3, Res.string.month_march),
    April(4, Res.string.month_april),
    May(5, Res.string.month_may),
    June(6, Res.string.month_june),
    July(7, Res.string.month_july),
    August(8, Res.string.month_august),
    September(9, Res.string.month_september),
    October(10, Res.string.month_october),
    November(11, Res.string.month_november),
    December(12, Res.string.month_december)
}

fun LocalDate.Companion.today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}

fun LocalDate?.orToday(): LocalDate =
    this ?: LocalDate.today().date