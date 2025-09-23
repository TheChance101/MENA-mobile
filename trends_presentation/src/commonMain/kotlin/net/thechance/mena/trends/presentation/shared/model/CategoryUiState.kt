package net.thechance.mena.trends.presentation.shared.model

internal data class CategoryUiState(
    val id: String? = null,
    val name: String = "",
    val emoji: String = "",
)

internal fun List<Selectable<CategoryUiState>>.toggleCategory(id: String) =
    map { if (it.value.id == id) it.copy(isSelected = !it.isSelected) else it }