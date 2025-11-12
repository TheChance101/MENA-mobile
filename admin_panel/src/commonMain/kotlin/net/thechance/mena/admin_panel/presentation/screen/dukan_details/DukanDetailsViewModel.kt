package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class DukanDetailsViewModel(
    @Provided
    private val dukanRepository: DukanRepository,
    @Provided
    private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseViewModel<DukanDetailsScreenState, DukanDetailEffect>(DukanDetailsScreenState()),
    DukanDetailsInteractionListener {
    init {
        getDukanDetails()
    }

    override fun onBackBtnClicked() {
        TODO("Not yet implemented")
    }

    override fun onChangeDukanStatusBtnClicked() {
        TODO("Not yet implemented")
    }

    private fun getDukanDetails() {
        tryToExecute(
            callee = { dukanRepository.getDukanDetails(Uuid.parse("3e2ac1b3-e322-465a-b454-1af7625ffae9")) },
            onSuccess = ::onGetDukanDetailsSuccess,
            onError = ::onGetDukanDetailsError,
            dispatcher = dispatcher
        )
    }

    private fun onGetDukanDetailsSuccess(dukan: Dukan) {
        updateState { it.copy(dukan = dukan.toUi()) }
    }

    private suspend fun onGetDukanDetailsError(error: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(error.getErrorSnackBarTitle()),
            message = stringProvider.getString(error.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    title = title,
                    message = message,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState ->
            oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false))
        }
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }
}