package net.thechance.mena.admin_panel.presentation.utils

import net.thechance.mena.admin_panel.presentation.model.menaPhoneFormats

fun formatPhoneNumber(rawNumber: String): String {
    val phoneFormat = menaPhoneFormats.firstOrNull {
        rawNumber.startsWith(it.callingCode)
    } ?: return rawNumber

    val nationalDigits = rawNumber
        .removePrefix(phoneFormat.callingCode)
        .filter { it.isDigit() }

    if (nationalDigits.isEmpty()) return rawNumber

    val maskPattern = phoneFormat.masks[nationalDigits.length]

    if (maskPattern == null) {
        return "${phoneFormat.callingCode} $nationalDigits"
    }

    val formattedNationalPart = applyMask(nationalDigits, maskPattern)
    return "${phoneFormat.callingCode} $formattedNationalPart"
}

private fun applyMask(digits: String, mask: String): String {
    val output = StringBuilder()
    var digitIndex = 0

    for (m in mask) {
        if (m == '#') {
            if (digitIndex < digits.length) {
                output.append(digits[digitIndex++])
            }
        } else {
            if (digitIndex < digits.length) {
                output.append(m)
            }
        }
    }

    return output.toString()
}