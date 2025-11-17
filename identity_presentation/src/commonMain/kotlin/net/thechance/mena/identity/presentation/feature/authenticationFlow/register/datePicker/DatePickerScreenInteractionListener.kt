package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.datePicker

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener

interface DatePickerScreenInteractionListener : BaseInteractionListener {
    fun onClickNext()
    fun onChangeDate(day: Int, month: Int, year: Int)
}