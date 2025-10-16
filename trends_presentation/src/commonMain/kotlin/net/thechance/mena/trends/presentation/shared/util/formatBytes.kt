package net.thechance.mena.trends.presentation.shared.util

import kotlin.math.roundToInt

internal fun formatBytes(
    bytes: Long,
    withUnit: Boolean = true
): String {
    val kb = 1024.0
    val mb = kb * 1024

    return when {
        bytes >= mb -> {
            val value = ((bytes / mb) * 10).roundToInt() / 10.0
            if (withUnit) "$value MB" else "$value"
        }
        bytes >= kb -> {
            val value = ((bytes / kb) * 10).roundToInt() / 10.0
            if (withUnit) "$value KB" else "$value"
        }
        else -> {
            if (withUnit) "$bytes B" else "$bytes"
        }
    }
}