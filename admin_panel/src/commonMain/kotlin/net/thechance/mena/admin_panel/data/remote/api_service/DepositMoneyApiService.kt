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

    @GET(COUNTRIES_END_POINT)
    suspend fun getCountries(
        @Header("Accept-Language") language: String = "en"
    ): Response<List<CountryDto>>

    private companion object Companion {
        const val COUNTRIES_END_POINT = "identity/authentication/countries"
        const val DEPOSIT_MONEY_END_POINT = "wallet/admin/deposit"
    }
}