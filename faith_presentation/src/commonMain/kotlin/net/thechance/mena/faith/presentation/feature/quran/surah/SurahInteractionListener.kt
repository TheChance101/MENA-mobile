package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.ui.text.TextLayoutResult

interface SurahInteractionListener {
    fun onAyahLongPress(ayahContent: String, ayahIndex: Int)
    fun onDismissActionButtons()
    fun onBackClick()
    fun onTextLayoutChanged(textLayoutResult: TextLayoutResult)
    fun onBookmarkClick(ayahNumber: Int)
    fun onCopyClick(ayahContent: String)
    fun onShareClick(ayahContent: String)

}