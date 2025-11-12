package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto

interface DepositApiService {
    @POST(DEPOSIT_END_POINT)
    suspend fun deposit(
        @Body deposit: DepositRequestDto
    ): Response<Unit>

    private companion object {
        const val DEPOSIT_END_POINT = "wallet/admin/balance/deposit"
    }
}