package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import net.thechance.mena.admin_panel.data.remote.dto.deposit.CountryDto
import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto

interface DepositMoneyApiService {
    @POST(DEPOSIT_MONEY_END_POINT)
    suspend fun depositMoney(@Body deposit: DepositRequestDto): Response<Unit>

    private companion object {
        const val DEPOSIT_MONEY_END_POINT = "wallet/admin/deposit"
    }
}