package net.thechance.mena.admin_panel.domain.entity.dukan

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Dukan(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: LocalDateTime,
    val categories: List<Category>,
    val activationStatus: ActivationStatus?,
    val status: Status
) {
    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED;

        companion object {
            fun valueOfOrNull(value: String?): ActivationStatus? {
                return runCatching {
                    value?.uppercase()?.let { valueOf(it) }
                }.getOrNull()
            }
        }
    }

    enum class Status {
        APPROVED,
        REJECTED,
        PENDING;

        companion object {
            fun valueOfOrDefault(value: String?): Status {
                return runCatching {
                    value?.uppercase()?.let { valueOf(it) } ?: PENDING
                }.getOrDefault(PENDING)
            }
        }
    }
}