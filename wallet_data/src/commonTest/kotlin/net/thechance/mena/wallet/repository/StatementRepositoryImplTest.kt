package net.thechance.mena.wallet.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import net.thechance.mena.wallet.data.repository.statement.datasource.local.StatementLocalDataSource
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSource
import net.thechance.mena.wallet.data.repository.statement.key
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class StatementRepositoryImplTest {

    private lateinit var statementRepository: StatementRepositoryImpl
    private lateinit var statementRemoteDataSource: StatementRemoteDataSource
    private lateinit var statementLocalDataSource: StatementLocalDataSource
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        statementRemoteDataSource = mock<StatementRemoteDataSource>(mode = MockMode.autofill)
        statementLocalDataSource = mock<StatementLocalDataSource>(mode = MockMode.autofill)
        statementRepository =
            StatementRepositoryImpl(statementRemoteDataSource, statementLocalDataSource)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `TransactionsPdf should return statement when API call is successful`() =
        runTest(testDispatcher) {
            everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
            everySuspend { statementRemoteDataSource.getTransactionPdf(null) } returns statement

            val result = statementRepository.getTransactionsPdf()

            assertContentEquals(statement, result)
        }

    @Test
    fun `TransactionsPdf should return last statement when TransactionsPdf has been called before`() =
        runTest(testDispatcher) {
            everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
            everySuspend { statementRemoteDataSource.getTransactionPdf(null) } returns statement

            statementRepository.getTransactionsPdf()
            val result = statementRepository.getTransactionsPdf()

            assertContentEquals(statement, result)
        }


    @Test
    fun `getTransactionsPdf should return cached statement`() = runTest(testDispatcher) {
        everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
        everySuspend { statementRemoteDataSource.getTransactionPdf(null) } returns statement

        statementRepository.getTransactionsPdf()
        val result = statementRepository.getTransactionsPdf()

        assertContentEquals(statement, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getTransactionsPdf should return null after expiration time`() = runTest(testDispatcher) {
        everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
        everySuspend { statementRemoteDataSource.getTransactionPdf(null) } returns statement

        statementRepository.getTransactionsPdf()
        val result = statementRepository.getTransactionsPdf()
        advanceUntilIdle()

        assertContentEquals(statement, result)
    }

    private companion object {
        val statement = ByteArray(5, { it.toByte() })
    }
}
