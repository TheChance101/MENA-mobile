package net.thechance.mena.identity.domain.util

enum class AppLanguage(val iso:String){
    ENGLISH("en"),
    ARABIC("ar"),
    DEFAULT("");

    companion object {
        fun fromIso(iso: String): AppLanguage {
            return when (iso.lowercase()) {
                ENGLISH.iso -> ENGLISH
                ARABIC.iso -> ARABIC
                else -> ENGLISH
            }
        }
    }

}