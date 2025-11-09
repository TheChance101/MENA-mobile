package net.thechance.mena.wallet.data.repository.balance

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.data.dto.remote.BalanceDto
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import org.koin.core.annotation.Single

@Single
class BalanceRepositoryImpl(
    private val networkClient: NetworkClient
) : BalanceRepository {

    private val balanceFlow = MutableStateFlow(0.0)

    override suspend fun getBalance(): Double {
        val balance = safeApiCall<BalanceDto> {
            networkClient.get(BALANCE_PATH)
        }.balance ?: 0.0
        balanceFlow.update { balance }
        return balance
    }

    override fun observeBalance(): Flow<Double> {
        if (balanceFlow.value == 0.0) {
            CoroutineScope(Dispatchers.IO).launch {
                getBalance()
            }
        }
        return balanceFlow
    }

    private companion object {
        const val BALANCE_PATH = "wallet/balance"
    }
}