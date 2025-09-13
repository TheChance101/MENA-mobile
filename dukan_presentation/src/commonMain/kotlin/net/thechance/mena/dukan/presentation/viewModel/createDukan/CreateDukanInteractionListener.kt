package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan

interface CreateDukanInteractionListener {

    fun onButtonClicked()
    fun onBackClicked()
    fun onColorClicked(color: Long)
    fun onStyleClicked(style: Dukan.Style)
}