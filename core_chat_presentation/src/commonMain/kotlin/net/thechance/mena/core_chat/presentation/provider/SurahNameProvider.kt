package net.thechance.mena.core_chat.presentation.provider

interface SurahNameProvider {
    suspend fun getSurahName(surahId: Int): String 
}