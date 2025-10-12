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
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.database.StatementWithMetaDataDto
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import net.thechance.mena.wallet.data.repository.statement.datasource.local.StatementLocalDataSource
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSource
import net.thechance.mena.wallet.data.repository.statement.key
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StatementRepositoryImplTest {

    private lateinit var statementRepository: StatementRepositoryImpl
    private lateinit var statementRemoteDataSource: StatementRemoteDataSource
    private lateinit var statementLocalDataSource: StatementLocalDataSource
    private lateinit var statementDao: StatementDao
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        statementRemoteDataSource = mock<StatementRemoteDataSource>(mode = MockMode.autofill)
        statementLocalDataSource = mock<StatementLocalDataSource>(mode = MockMode.autofill)
        statementDao = mock<StatementDao>(mode = MockMode.autofill)
        statementRepository =
            StatementRepositoryImpl(statementRemoteDataSource, statementLocalDataSource, statementDao)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `getStatementWithMetadata should return statement with metadata when API call is successful`() =
        runTest(testDispatcher) {
            everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
            everySuspend { statementRemoteDataSource.getStatementWithMetaData(null) } returns statementDto

            val result = statementRepository.getStatementWithMetadata()

            assertContentEquals(pdfBytes, result.byteArray)
            assertEquals(99.80, result.totalInflows)
            assertEquals(520.75, result.totalOutflows)
            assertEquals(LocalDate.parse("2025-09-25"), result.startDate)
            assertEquals(LocalDate.parse("2025-10-06"), result.endDate)
        }

    @Test
    fun `getStatementWithMetadata should return cached statement when called multiple times`() =
        runTest(testDispatcher) {
            everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
            everySuspend { statementRemoteDataSource.getStatementWithMetaData(null) } returns statementDto

            val firstResult = statementRepository.getStatementWithMetadata()

            everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns pdfBytes
            val secondResult = statementRepository.getStatementWithMetadata()

            assertContentEquals(firstResult.byteArray, secondResult.byteArray)
        }


    @Test
    fun `getCachedStatement should return cached statement from local data source`() = runTest(testDispatcher) {
        everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns pdfBytes

        val result = statementRepository.getCachedStatement(TransactionFilterParams())

        assertContentEquals(pdfBytes, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getStatementWithMetadata should fetch new data after expiration time`() = runTest(testDispatcher) {
        everySuspend { statementLocalDataSource.getStatement(TransactionFilterParams().key()) } returns null
        everySuspend { statementRemoteDataSource.getStatementWithMetaData(null) } returns statementDto

        val firstResult = statementRepository.getStatementWithMetadata()
        advanceUntilIdle()

        val secondResult = statementRepository.getStatementWithMetadata()

        assertContentEquals(pdfBytes, firstResult.byteArray)
        assertContentEquals(pdfBytes, secondResult.byteArray)
    }

    private companion object {
        val pdfBytes = ByteArray(5) { it.toByte() }
        val statementDto = StatementWithMetaDataDto(
            byteArray = pdfBytes,
            startDate = "2025-09-25",
            endDate = "2025-10-06",
            totalInflows = 99.80,
            totalOutflows = 520.75
        )
    }
}