import net.thechance.mena.trends.domain.util.Logger
import net.thechance.mena.trends.presentation.shared.util.StateHandle

//package net.thechance.mena.trends.presentation.screen
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.TestDispatcher
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.setMain
//import net.thechance.mena.trends.domain.util.Logger
//import net.thechance.mena.trends.presentation.shared.util.StateHandle
//import org.koin.core.context.startKoin
//import org.koin.core.context.stopKoin
//import org.koin.dsl.module
//import kotlin.test.AfterTest
//import kotlin.test.BeforeTest
//
//@OptIn(ExperimentalCoroutinesApi::class)
//open class MainDispatcherTest(
//    internal val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
//) {
//    @BeforeTest
//    fun setUp() {
//        startKoin {
//            modules(module { single<Logger> { FakeLogger() } })
//        }
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    @AfterTest
//    fun tearDown() {
//        stopKoin()
//        Dispatchers.resetMain()
//    }
//}
//
class FakeLogger : Logger {
    override fun logError(tag: String, message: String) {}
}

class FakeStateHandle : StateHandle {
    private val state = mutableMapOf<String, Any?>()

    override fun <T> get(key: String): T? = state[key] as? T
    override fun <T> set(key: String, value: T) {
        state[key] = value
    }
}
