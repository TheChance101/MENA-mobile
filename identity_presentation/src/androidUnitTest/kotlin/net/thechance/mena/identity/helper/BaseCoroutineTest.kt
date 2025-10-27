package net.thechance.mena.identity.helper

import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest


@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseCoroutineTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    open fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    open fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }
}