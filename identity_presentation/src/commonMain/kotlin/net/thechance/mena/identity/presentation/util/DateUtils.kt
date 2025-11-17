package net.thechance.mena.identity.presentation.util

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun getDefaultMonthNames(): List<String> {
    return listOf(
        stringResource(Res.string.month_january),
        stringResource(Res.string.month_february),
        stringResource(Res.string.month_march),
        stringResource(Res.string.month_april),
        stringResource(Res.string.month_may),
        stringResource(Res.string.month_june),
        stringResource(Res.string.month_july),
        stringResource(Res.string.month_august),
        stringResource(Res.string.month_september),
        stringResource(Res.string.month_october),
        stringResource(Res.string.month_november),
        stringResource(Res.string.month_december)
    )
}

fun LocalDate.toFormattedDate(): String {
    return this.format(
        LocalDate.Format {
            day(padding = Padding.ZERO)
            char('/')
            monthNumber()
            char('/')
            year()
        }
    )
}

