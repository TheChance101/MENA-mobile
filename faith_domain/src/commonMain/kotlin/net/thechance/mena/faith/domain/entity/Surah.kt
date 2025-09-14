package net.thechance.mena.faith.domain.entity

data class Surah(
    val id: Int,
    val order: SurahOrder,
    val name: String,
    val ayahCount: Int,
    val isMakkia: Boolean = true,
) {
    enum class SurahOrder(order: Int) {
        ALFATIHA(1),
    }
}
