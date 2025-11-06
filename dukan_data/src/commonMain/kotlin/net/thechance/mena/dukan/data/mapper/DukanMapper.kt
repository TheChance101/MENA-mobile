package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.dukan.CreateDukanRequest
import net.thechance.mena.dukan.data.dto.dukan.DukanCategoryDto
import net.thechance.mena.dukan.data.dto.dukan.DukanColorDto
import net.thechance.mena.dukan.data.dto.dukan.DukanDetailsDto
import net.thechance.mena.dukan.data.dto.dukan.MyDukanStatusDto
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.MyDukanStatus
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
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

@OptIn(ExperimentalUuidApi::class)
fun List<DukanCategoryDto>.toCategoryList(): List<Category> {
    return map {
        Category(
            id = it.id,
            name = it.title,
            imageUrl = it.icon
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
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


@OptIn(ExperimentalUuidApi::class)
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
