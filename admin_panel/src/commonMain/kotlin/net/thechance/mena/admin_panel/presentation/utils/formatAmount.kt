package net.thechance.mena.admin_panel.presentation.utils

fun formatAmount(input: String): String {
    val cleaned = input.replace(",", "")
    val parts = cleaned.split(".")
    val integerPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    return if (parts.size > 1) {
        val decimalPart = parts[1].take(2)
        "$integerPart.$decimalPart"
    } else {
        integerPart
    }
}