@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.CreateDukanRequest
import net.thechance.mena.dukan.data.dto.dukan.DukanActivationStatusResponse
import net.thechance.mena.dukan.data.dto.dukan.DukanCategoryDto
import net.thechance.mena.dukan.data.dto.dukan.DukanColorDto
import net.thechance.mena.dukan.data.dto.dukan.DukanDetailsDto
import net.thechance.mena.dukan.data.dto.dukan.MyDukanStatusDto
import net.thechance.mena.dukan.data.dto.dukan.TopDiscountedDukanDto
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import kotlin.uuid.ExperimentalUuidApi

fun Dukan.toCreateDukanRequest(): CreateDukanRequest {
    return CreateDukanRequest(
        name = name,
        categoryIds = categories.map { it.id }.toSet(),
        address = address,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude,
        colorId = color.id,
        style = style.name
    )
}

fun List<DukanCategoryDto>.toCategoryList(): List<Category> {
    return map {
        Category(
            id = it.id,
            name = it.title,
            imageUrl = it.icon
        )
    }
}

fun List<DukanColorDto>.toColorsList(): List<Color> {
    return map {
        Color(
            id = it.id,
            hexCode = it.hexCode
        )
    }
}


fun MyDukanStatusDto.toMyDukanStatus(): MyDukanStatus {
    return MyDukanStatus(
        status = Dukan.Status.valueOf(status),
        dukanName = dukanName
    )
}


fun DukanDetailsDto.toDukan(): Dukan {
    return Dukan(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        address = address,
        coordinates = Dukan.Coordinates(
            latitude = latitude,
            longitude = longitude
        ),
        color = Color(
            id = color.id,
            hexCode = color.hexCode
        ),
        style = Dukan.Style.valueOf(style),
        categories = emptySet(),
        status = Dukan.Status.APPROVED
    )
}

fun TopDiscountedDukanDto.toEntity(): TopDiscountedDukanPreview{
    return TopDiscountedDukanPreview(
        id = id,
        imageUrl = imageUrl,
        discount = discount.toInt()
    )
}

fun DukanActivationStatusResponse.toActivationStatus(): Dukan.Activation {
    return Dukan.Activation(
        activationStatus = Dukan.ActivationStatus.valueOf(status),
        reason = reason
    )
}