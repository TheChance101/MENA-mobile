package net.thechance.mena.identity.presentation.util

import kotlinx.datetime.YearMonth
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.month_april
import mena.identity_presentation.generated.resources.month_august
import mena.identity_presentation.generated.resources.month_december
import mena.identity_presentation.generated.resources.month_february
import mena.identity_presentation.generated.resources.month_january
import mena.identity_presentation.generated.resources.month_july
import mena.identity_presentation.generated.resources.month_june
import mena.identity_presentation.generated.resources.month_march
import mena.identity_presentation.generated.resources.month_may
import mena.identity_presentation.generated.resources.month_november
import mena.identity_presentation.generated.resources.month_october
import mena.identity_presentation.generated.resources.month_september
import org.jetbrains.compose.resources.StringResource

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