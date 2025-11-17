@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.dto.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class OrderDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("orderNumber")
    val orderNumber: Long,
    @SerialName("time")
    val time: String,
    @SerialName("orderItemResponse")
    val orderItemResponse: List<OrderItemDto>,
    @SerialName("discount")
    val discount: Double,
    @SerialName("platformFeesAmount")
    val platformFees : Double,
    @SerialName("totalAmount")
    val totalAmount : Double,
    @SerialName("addersLine")
    val addersLine: String,
    @SerialName("customerLatitude")
    val customerLatitude: Double,
    @SerialName("customerLongitude")
    val customerLongitude: Double,
    @SerialName("latitude")
    val dukanLatitude: Double,
    @SerialName("longitude")
    val dukanLongitude: Double,
    @SerialName("customerName")
    val customerName: String,
    @SerialName("customerNumber")
    val customerNumber: String,
    @SerialName("customerImage")
    val customerImage: String? = null,
    @SerialName("isDukanOwner")
    val isDukanOwner: Boolean
)