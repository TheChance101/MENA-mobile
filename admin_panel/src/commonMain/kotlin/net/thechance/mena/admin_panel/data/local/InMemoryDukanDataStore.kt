package net.thechance.mena.admin_panel.data.local

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
class InMemoryDukanDataStore {
    private var dukan: Dukan? = null

    fun storeDukan(value: Dukan) {
        dukan = value
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getDukan(): Dukan? = Dukan(
        id = Uuid.parse("3e2ac1b3-e322-465a-b454-1af7625ffae9"),
        name = "noureeeeee",
        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwY3v_vNKWFQrfnCWUFM3-dKzOVj2Kcm5chg&s",
        address = "xssssssssss",
        latitude = 12.2,
        longitude = 12.2,
        createdAt = LocalDateTime(2020,12,2,10,12,10,22),
        categories = listOf(),
        activationStatus = Dukan.ActivationStatus.ACTIVATED,
        status = Dukan.Status.APPROVED
    )

    fun clear() {
        dukan = null
    }
}
