package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.ui.text.TextLayoutResult

interface SurahInteractionListener {
    fun onBackClick()
    fun onDismissActionButtons()
    fun onCopyClick(ayahContent: String)
    fun onShareClick(ayahContent: String)
    fun onBookmarkClick(ayahNumber: Int)
    fun onAyahLongPress(ayahContent: String, ayahIndex: Int)
    fun onTextLayoutChanged(textLayoutResult: TextLayoutResult)
}