package net.thechance.mena.admin_panel.domain.repository.deposit

import net.thechance.mena.admin_panel.domain.model.Country

interface DepositRepository {
    suspend fun deposit(phoneNumber :String , amount :Double)
    suspend fun getCountries(language: String = "en"):List<Country>
}