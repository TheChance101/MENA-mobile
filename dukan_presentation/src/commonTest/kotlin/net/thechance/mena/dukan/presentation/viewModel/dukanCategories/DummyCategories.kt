package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.domain.entity.Category
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
val dummyCategories = List(10) { index ->
    Category(
        id = Uuid.random(),
        name = "Category ${index + 1}",
        imageUrl = fakeImageUrl,
    )
}
private const val fakeImageUrl = "https://cdn-icons-png.flaticon.com/512/259/259552.png"