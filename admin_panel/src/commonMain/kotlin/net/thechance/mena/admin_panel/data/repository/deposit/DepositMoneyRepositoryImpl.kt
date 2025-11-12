package net.thechance.mena.admin_panel.data.repository.deposit

import net.thechance.mena.admin_panel.data.mapper.deposit.toEntity
import net.thechance.mena.admin_panel.data.remote.api_service.DepositMoneyApiService
import net.thechance.mena.admin_panel.data.remote.dto.deposit.CountryDto
import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import org.koin.core.annotation.Single

@Single
class DepositMoneyRepositoryImpl(
    private val depositMoneyApiService: DepositMoneyApiService
) : DepositMoneyRepository {
    override suspend fun depositMoney(phoneNumber: String, amount: Double) {
        executeApiSafely<Unit> {
            depositMoneyApiService.depositMoney(DepositRequestDto(
                phoneNumber,
                amount
            ))
        }
    }

    override suspend fun getCountries(language: String): List<Country> {
        return executeApiSafely<List<CountryDto>> {
            depositMoneyApiService.getCountries(language)
        }.map { it.toEntity() }
    }
}