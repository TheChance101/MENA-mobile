package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

fun formatCountdown(remainingMillis: Long): String {
    val remainingSeconds = remainingMillis / 1000

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    return "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${seconds.toString().padStart(2, '0')}"
}