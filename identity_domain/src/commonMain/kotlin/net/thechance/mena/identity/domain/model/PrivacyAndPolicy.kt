package net.thechance.mena.identity.domain.model

import kotlinx.datetime.LocalDate

data class PrivacyAndPolicy(
    val updateDate: LocalDate,
    val sections: List<Section>
)
data class Section(
    val title:String,
    val content:String
)