package net.thechance.mena.identity.presentation.screen.editProfile

import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface EditUserProfileInteractionListener : BaseInteractionListener {
    fun onChangeFirstName(firstName: String)
    fun onChangeLastName(lastName: String)
    fun onChangeUsername(username: String)
    fun onChangeGender(gender: Gender)
    fun onClickSaveButton()
    fun onClickCancelButton()
    fun onChangeDate(day: Int, month: Int, year: Int)
    fun clearErrorMessage()
}