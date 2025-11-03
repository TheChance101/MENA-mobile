package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenUIEffect
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenViewModel
import org.junit.Before
import org.junit.Test

class SelectGenderScreenViewModelTest: BaseCoroutineTest() {
    private lateinit var selectGenderScreenViewModel: SelectGenderScreenViewModel

    @Before
    fun setup() {
        selectGenderScreenViewModel = SelectGenderScreenViewModel()
    }

    @Test
    fun `onChangeGender should update gender`() {
        val gender = Gender.MALE

        selectGenderScreenViewModel.onChangeGender(gender)
    }

    @Test
    fun `onClickRegister should navigate to account created screen`() = runTest {
        selectGenderScreenViewModel.effect.test {
            selectGenderScreenViewModel.onClickRegister()
            assert(awaitItem() == SelectGenderScreenUIEffect.NavigateToAccountCreatedScreen)
        }
    }
}