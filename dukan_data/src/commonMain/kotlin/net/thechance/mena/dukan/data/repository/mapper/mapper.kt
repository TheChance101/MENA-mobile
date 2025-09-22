package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.repository.dto.CreateDukanRequest
import net.thechance.mena.dukan.data.repository.dto.DukanCategoryDto
import net.thechance.mena.dukan.data.repository.dto.DukanColorDto
import net.thechance.mena.dukan.data.repository.dto.MyDukanStatusDto
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.MyDukanStatus

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