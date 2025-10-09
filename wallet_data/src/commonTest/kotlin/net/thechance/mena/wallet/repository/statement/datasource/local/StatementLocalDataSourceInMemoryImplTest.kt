package net.thechance.mena.wallet.repository.statement.datasource.local

import kotlinx.coroutines.test.runTest
import net.thechance.mena.wallet.data.repository.statement.CachedTransactionsPdfDto
import net.thechance.mena.wallet.data.repository.statement.datasource.local.StatementLocalDataSourceInMemoryImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class StatementLocalDataSourceInMemoryImplTest {

    lateinit var statementLocalDataSourceInMemoryImpl: StatementLocalDataSourceInMemoryImpl

    @Test
    fun `saveStatement should add statement to the list`() = runTest {
        setupTest()

        assertEquals(1, statementLocalDataSourceInMemoryImpl.statements.size)
        assertEquals(dto1, statementLocalDataSourceInMemoryImpl.statements.first())
    }

    @Test
    fun `getStatement should get statement by key`() = runTest {
        setupTest()

        val res =  statementLocalDataSourceInMemoryImpl.getStatement(key1)

        assertEquals(pdf1, res)
    }

    @Test
    fun `getStatement should return null when there is no statement with this key`() = runTest {
        setupTest()

        val res =  statementLocalDataSourceInMemoryImpl.getStatement(key2)

        assertEquals(null, res)
    }

    @Test
    fun `clearStatement should delete statement by key`() = runTest {
        setupTest()

        statementLocalDataSourceInMemoryImpl.clearStatement(key1)

        assertEquals(0, statementLocalDataSourceInMemoryImpl.statements.size)
    }

    @Test
    fun `clearAllStatements should delete all statements`() = runTest {
        setupTest()

        statementLocalDataSourceInMemoryImpl.saveStatement(dto1.copy(key = key2))
        statementLocalDataSourceInMemoryImpl.clearAllStatements()

        assertEquals(0, statementLocalDataSourceInMemoryImpl.statements.size)
    }

    @Test
    fun `clearExpiredStatements should delete all expired statements`() = runTest {
        setupTest()

        statementLocalDataSourceInMemoryImpl.saveStatement(dto1.copy(key = key2))
        statementLocalDataSourceInMemoryImpl.clearExpiredStatements(Instant.fromEpochMilliseconds(2000))

        assertEquals(0, statementLocalDataSourceInMemoryImpl.statements.size)
    }

    private fun setupTest() = runTest{
        statementLocalDataSourceInMemoryImpl = StatementLocalDataSourceInMemoryImpl()
        statementLocalDataSourceInMemoryImpl.saveStatement(dto1)
    }

    companion object{
        val pdf1 = ByteArray(12)
        val key1 = "key1"
        val key2 = "key2"
        val timestamp1 = Instant.fromEpochMilliseconds(1000)
        val dto1 = CachedTransactionsPdfDto(pdf1, key1, timestamp1)
    }
}