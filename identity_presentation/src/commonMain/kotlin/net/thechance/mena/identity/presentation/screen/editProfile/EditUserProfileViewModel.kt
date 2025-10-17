package net.thechance.mena.identity.presentation.screen.editProfile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState

class EditUserProfileViewModel(
    private val userRepository: UserRepository,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<EditUserProfileUIState, EditUserProfileUIEffect>(EditUserProfileUIState()),
    EditUserProfileInteractionListener {

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        tryToCollect(
            function = { userRepository.getUser() },
            onNewValue = ::updateUserInfo,
            onError = ::onErrorOccurred,
            dispatcher = dispatcher
        )
    }

    private fun updateUserInfo(user: User) {
        updateState {
            copy(
                username = user.username.lowercase(),
                firstName = user.firstName,
                lastName = user.lastName,
                profileImageUrl = user.profileImageUrl,
                birthDate = user.birthDate,
                gender = user.gender,
            )
        }
    }

    override fun onChangeFirstName(firstName: String) {
        updateState { copy(firstName = firstName) }
    }

    override fun onChangeLastName(lastName: String) {
        updateState { copy(lastName = lastName) }
    }

    override fun onChangeUsername(username: String) {
        updateState { copy(username = username) }
    }

    override fun onChangeGender(gender: Gender) {
        updateState { copy(gender = gender) }
    }

    override fun onClickSaveButton() {
        tryToExecute(
            function = ::onSave,
            onError = ::onErrorOccurred,
            onSuccess = ::onSaveSuccess
        )
    }

    private suspend fun onSave() {
        val value = state.value
        val user = User(
            firstName = value.firstName,
            lastName = value.lastName,
            username = value.username.lowercase(),
            profileImageUrl = value.profileImageUrl,
            birthDate = value.birthDate,
            gender = value.gender,
        )
        userRepository.updateUser(user)
    }

    private fun onSaveSuccess() {
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile)
    }

    override fun onClickCancelButton() {
        sendNewEffect(EditUserProfileUIEffect.NavigateBackToProfile)
    }

    override fun onChangeDate(day: Int, month: Int, year: Int) {
        updateState { copy(birthDate = LocalDate(year, month, day)) }
    }

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun onErrorOccurred(errorState: ErrorState) {
        updateState { copy(isLoading = false, errorMessage = errorState.toString()) }
    }
}