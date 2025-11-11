package net.thechance.mena.faith.presentation.feature.quran.tilwah.args

interface TilawahArgs {
    val surahId: Int?
        get() = null
    val isSelectedShown: Boolean
        get() = false

    val isSwipeToDeleteEnabled: Boolean
        get() = false
}

