package net.thechance.mena.wallet.data.repository.balance

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import net.thechance.mena.wallet.data.dto.remote.BalanceDto
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.utils.orZero
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import org.koin.core.annotation.Single

@Single
class BalanceRepositoryImpl(
    private val networkClient: NetworkClient
) : BalanceRepository {

    private val balanceFlow = MutableStateFlow<Double?>(null)

    override suspend fun getBalance(): Double {
        return safeApiCall<BalanceDto> {
            networkClient.get(BALANCE_PATH)
        }.balance.orZero().also { balance ->
            balanceFlow.update { balance }
        }
    }

    override fun observeBalance(): Flow<Double?> {
        return balanceFlow.onSubscription {
            // getBalance is wrapped with runCatching to fetch balance on subscription without propagating exceptions to the flow
            runCatching { getBalance() }
        }
    }

    private companion object {
        const val BALANCE_PATH = "wallet/balance"
    }
}