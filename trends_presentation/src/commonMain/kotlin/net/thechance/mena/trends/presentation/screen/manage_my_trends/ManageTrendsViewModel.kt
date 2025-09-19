package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.shared.base.BaseViewModel
import net.thechance.mena.trends.presentation.shared.base.createPager
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided


@KoinViewModel
class ManageTrendsViewModel(
    @Provided private val repository: ReelsRepository,
) : BaseViewModel<ManageTrendsScreenState,
        ManageTrendsUiEffect>(ManageTrendsScreenState()),
    ManageTrendsInteractionListener {

    init {
        getReels()
    }

    private fun getReels() {
        tryToExecute(
            block = {
                createPager(
                    scope = viewModelScope,
                    onError = {},
                    loadPage = { page -> repository.getAllReels(page) }
                )
            },
            onSuccess = ::onGetReelsSuccess,
            onError = {},
            onStart = { updateState { copy(isLoading = true) } },
            onEnd = { updateState { copy(isLoading = false) } }
        )
    }

    private fun onGetReelsSuccess(reelsFlow: Flow<PagingData<Reel>>) {
        val uiReelsFlow = reelsFlow.map { pagingData: PagingData<Reel> ->
            pagingData.map { reel -> reel.toUiState() }
        }
        updateState { copy(isLoading = false, reels = uiReelsFlow) }
    }

    override fun onReelItemClick(reelId: String) {
        sendEffect(ManageTrendsUiEffect.NavigateToTrend(reelId))
    }

    override fun onBackClick() {
        sendEffect(ManageTrendsUiEffect.NavigateBack)
    }
}