package net.thechance.mena.wallet.repository

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.wallet.repository.utils.createBalanceRepository
import net.thechance.mena.wallet.repository.utils.errorResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BalanceRepositoryImplTest {

    @Test
    fun `getBalance returns balance when API call is successful`() = runTest {
        val repository = createBalanceRepository()

        val result = repository.getBalance()

        assertEquals(150.75, result)
    }

    @Test
    fun `getBalance throws exception when API call fails`() = runTest {
        val repository = createBalanceRepository {
            errorResponse(HttpStatusCode.InternalServerError, "Server error")
        }

        assertFailsWith<Exception> {
            repository.getBalance()
        }
    }
}
