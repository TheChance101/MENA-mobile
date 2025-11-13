package net.thechance.mena.admin_panel.domain.repository.depositMoney

import net.thechance.mena.admin_panel.domain.model.Country

interface DepositMoneyRepository {
    suspend fun depositMoney(phoneNumber :String, amount :Double)
    suspend fun getCountries(language: String = "en"):List<Country>
}