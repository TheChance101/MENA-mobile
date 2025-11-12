package net.thechance.mena.admin_panel.data.repository.deposit

import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.remote.api_service.DepositApiService
import net.thechance.mena.admin_panel.data.remote.dto.deposit.DepositRequestDto
import kotlin.test.BeforeTest
import kotlin.test.Test

class DepositRepositoryImplTest {

    private lateinit var depositApiService: DepositApiService
    private lateinit var depositRepository: DepositRepositoryImpl

    @BeforeTest
    fun setup() {
        depositApiService = mock<DepositApiService>(mode = MockMode.autofill)
        depositRepository = DepositRepositoryImpl(depositApiService)
    }

    @Test
    fun `deposit should call api successfully`() = runTest {
        val fakeParams = DepositRequestDto(
            phoneNumber = "01012345678",
            amount = 100.0
        )

        everySuspend {
            depositApiService.deposit(fakeParams)
        } returns successfulEmptyResponse()

        depositRepository.deposit("01012345678" , 100.0)
    }

    private companion object {
        @OptIn(InternalAPI::class)
        private fun successfulEmptyResponse(): Response<Unit> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.OK
            }
            return Response.success(Unit, mockHttpResponse) as Response<Unit>
        }
    }
}
