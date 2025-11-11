package net.thechance.mena.admin_panel.data.mapper

fun Double?.orZero(): Double = this ?: 0.0
fun Int?.orZero(): Int = this ?: 0