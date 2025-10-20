package net.thechance.mena.wallet.data.repository.balance

import net.thechance.mena.wallet.data.dto.remote.BalanceDto
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import org.koin.core.annotation.Single

@Single
class BalanceRepositoryImpl(
    private val networkClient: NetworkClient
) : BalanceRepository {

    override suspend fun getBalance() = safeApiCall<BalanceDto> {
        networkClient.get(BALANCE_PATH)
    }.balance ?: 0.0

    private companion object {
        const val BALANCE_PATH = "wallet/balance"
    }
}