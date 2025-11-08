package net.thechance.mena.faith.presentation.utils.extentions

fun String.takeCityAndCountry(): String =
    this.split(",").map { it.trim() }.takeLast(2).joinToString(", ")