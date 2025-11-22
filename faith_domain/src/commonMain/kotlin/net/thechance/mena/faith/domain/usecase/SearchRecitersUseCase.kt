package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.model.Reciter

class SearchRecitersUseCase {

    operator fun invoke(query: String, reciters: List<Reciter>): List<Reciter> {
        if (query.isBlank()) return reciters
        return reciters.filter { it.name.contains(query, ignoreCase = true) }
    }
}