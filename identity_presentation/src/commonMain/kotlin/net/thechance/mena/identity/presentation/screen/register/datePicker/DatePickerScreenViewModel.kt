package net.thechance.mena.identity.presentation.screen.register.datePicker

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DatePickerScreenViewModel(
    private val ageValidator: AgeValidator,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val registerUIState: RegisterUIState,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<DatePickerScreenUIState, DatePickerScreenUIEffect>(
        DatePickerScreenUIState()
    ), DatePickerScreenInteractionListener {

    private var isInitialized = false
    private var savedDate: LocalDate? = null

    init {
        loadSavedData()
    }

    override fun onClickNext() {
        state.value.selectedDate.let { date ->
            sendNewEffect(createNavigationEffect(date))
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onChangeDate(day: Int, month: Int, year: Int) {
        val selectedDate = LocalDate(year, month, day)
        val currentDate = state.value.selectedDate

        if (shouldProcessDateChange(selectedDate, currentDate)) {
            updateDateState(selectedDate)
            saveBirthDateIfValid(selectedDate)
        }
    }

    private fun loadSavedData() {
        tryToExecute(
            function = { registrationDraftRepository.getDraft(registerUIState.phoneNumber) },
            onSuccess = ::handleSavedDraft,
            dispatcher = dispatcher
        )
    }

    private fun handleSavedDraft(savedDraft: RegistrationDraft?) {
        savedDraft?.birthDate?.let { date ->
            savedDate = date
            updateStateWithSavedDate(date)
            markInitializedAfterDelay()
        } ?: markInitialized()
    }

    private fun updateStateWithSavedDate(date: LocalDate) {
        updateState {
            copy(
                selectedDate = date,
                isNextEnabled = ageValidator.isValid(date)
            )
        }
    }

    private fun markInitializedAfterDelay() {
        screenModelScope.launch {
            delay(100)
            isInitialized = true
        }
    }

    private fun markInitialized() {
        isInitialized = true
    }

    private fun createNavigationEffect(selectedDate: LocalDate) =
        DatePickerScreenUIEffect.NavigateToSelectGender(
            registerUIState = registerUIState.copy(birthDate = selectedDate),
        )

    @OptIn(ExperimentalTime::class)
    private fun shouldProcessDateChange(selectedDate: LocalDate, currentDate: LocalDate?): Boolean {
        return isInitialized && !shouldIgnoreDateChange(selectedDate, currentDate)
    }

    @OptIn(ExperimentalTime::class)
    private fun shouldIgnoreDateChange(selectedDate: LocalDate, currentDate: LocalDate?): Boolean {
        return currentDate == selectedDate || isInitialWheelUpdate(selectedDate, currentDate)
    }

    @OptIn(ExperimentalTime::class)
    private fun isInitialWheelUpdate(selectedDate: LocalDate, currentDate: LocalDate?): Boolean {
        return savedDate != null &&
                currentDate == savedDate &&
                selectedDate == getTodayDate() &&
                selectedDate != savedDate
    }

    @OptIn(ExperimentalTime::class)
    private fun getTodayDate() =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun updateDateState(selectedDate: LocalDate) {
        updateState { copy(selectedDate = selectedDate) }
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        state.value.selectedDate.let { date ->
            updateState { copy(isNextEnabled = ageValidator.isValid(date)) }
        }
    }

    private fun saveBirthDateIfValid(birthDate: LocalDate) {
        if (ageValidator.isValid(birthDate)) {
            saveBirthDate(birthDate)
        }
    }

    private fun saveBirthDate(birthDate: LocalDate) {
        tryToExecute(
            function = {
                val draft = registrationDraftRepository.getDraft(registerUIState.phoneNumber) ?: RegistrationDraft()
                registrationDraftRepository.saveDraft(registerUIState.phoneNumber, draft.copy(birthDate = birthDate))
            },
            dispatcher = dispatcher
        )
    }
}