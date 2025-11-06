package net.thechance.mena.faith.presentation.feature.quran.reciter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class ReciterSearchViewModel(
    private val repository: QuranRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ReciterSearchUiState, ReciterSearchEffect>(
    ReciterSearchUiState()
), ReciterSearchInteractionListener {
    override fun onBackClick() = sendEffect(ReciterSearchEffect.NavigateBack)
    override fun onClearQueryClick() {
        TODO("Not yet implemented")
    }

    override fun onQueryChange(query: String) {
        TODO("Not yet implemented")
    }

    override fun onSearchResultClick(reciterId: Int) {
        TODO("Not yet implemented")
    }
}