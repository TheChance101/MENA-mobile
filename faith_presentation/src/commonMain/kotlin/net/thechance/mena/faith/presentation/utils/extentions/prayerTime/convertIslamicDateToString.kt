package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import androidx.compose.runtime.Composable
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.hijri_months
import net.thechance.mena.faith.presentation.utils.IslamicDate
import org.jetbrains.compose.resources.stringArrayResource

@Composable
fun IslamicDate.convertIslamicDateToString(): String {
    val months = stringArrayResource(Res.array.hijri_months)
    val monthName = months.getOrNull(month - 1) ?: "Unknown"
    return "$day $monthName $year"
}