package net.thechance.mena.admin_panel.data.repository

import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.data.local.InMemoryDukanDataStore
import net.thechance.mena.admin_panel.data.mapper.parseLocalDateTimeOrDefault
import net.thechance.mena.admin_panel.data.remote.api_service.DukanApiService
import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.data.repository.dukan.DukanRepositoryImpl
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import net.thechance.mena.admin_panel.domain.model.DukanQueryParams
import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.domain.model.SortDirection
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DukanRepositoryImplTest {

    private lateinit var dukanApiService: DukanApiService
    private lateinit var dukanRepository: DukanRepositoryImpl
    private lateinit var inMemoryDukanDataStore: InMemoryDukanDataStore

    @BeforeTest
    fun setup() {
        dukanApiService = mock<DukanApiService>(mode = MockMode.autofill)
        inMemoryDukanDataStore = InMemoryDukanDataStore()
        dukanRepository = DukanRepositoryImpl(dukanApiService, inMemoryDukanDataStore)
    }


    @Test
    fun `getDukans should return mapped dukans paged result on success`() = runTest {
        everySuspend {
            dukanApiService.getDukans(any(), any(), any(), any(), any())
        } returns successfulResponse(DUKAN_PAGED)

        val result = dukanRepository.getDukans(SUCCESS_STATE_QUERY_PARAMS)

        assertEquals(1, result.items.size)
        assertEquals("Test Dukan", result.items.first().name)
        assertEquals(1, result.totalPages)
        assertEquals(0, result.currentPage)
    }

    @Test
    fun `getDukans should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.getDukans(any(), any(), any(), any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.getDukans(UNAUTHORIZED_EXCEPTION_QUERY_PARAMS)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `getDukans should handle empty results`() = runTest {
        everySuspend {
            dukanApiService.getDukans(any(), any(), any(), any(), any())
        } returns successfulResponse(EMPTY_RESULT_RESPONSE)

        val result = dukanRepository.getDukans(EmptyResultqueryParams)

        assertEquals(0, result.items.size)
    }


    @Test
    fun `getDukanShelves should return mapped shelves paged result on success`() = runTest {
        everySuspend {
            dukanApiService.getDukanShelves(any(), any(), any())
        } returns successfulResponse(SHELF_PAGED)

        val result = dukanRepository.getDukanShelves(FAKE_UUID, page = 0, size = 10)

        assertEquals(1, result.items.size)
        assertEquals("Top Shelf", result.items.first().title)
    }

    @Test
    fun `getDukanShelves should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.getDukanShelves(any(), any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.getDukanShelves(Uuid.random(), page = 0, size = 10)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `getDukanShelves should handle multiple pages`() = runTest {
        everySuspend {
            dukanApiService.getDukanShelves(any(), any(), any())
        } returns successfulResponse(MULTI_PAGE_RESPONSE)

        val result = dukanRepository.getDukanShelves(FAKE_UUID, page = 1, size = 10)

        assertEquals(3, result.totalPages)
    }


    @Test
    fun `getShelfProducts should return mapped products paged result on success`() = runTest {
        everySuspend {
            dukanApiService.getShelfProducts(any(), any(), any())
        } returns successfulResponse(PRODUCT_PAGED)

        val result = dukanRepository.getShelfProducts(FAKE_SHELF_ID, page = 0, size = 10)

        assertEquals(1, result.items.size)
        assertEquals("Product 1", result.items.first().name)
        assertEquals(100.0, result.items.first().finalPrice)
        assertEquals(80.0, result.items.first().basePrice)
    }

    @Test
    fun `getShelfProducts should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.getShelfProducts(any(), any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.getShelfProducts(Uuid.random(), page = 0, size = 10)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `getShelfProducts should handle empty product list`() = runTest {
        everySuspend {
            dukanApiService.getShelfProducts(any(), any(), any())
        } returns successfulResponse(EMPTY_PRODUCT_RESPONSE)

        val result = dukanRepository.getShelfProducts(FAKE_SHELF_ID, page = 0, size = 10)

        assertEquals(0, result.items.size)
    }

    @Test
    fun `getShelfProducts should correctly map multiple products`() = runTest {
        everySuspend {
            dukanApiService.getShelfProducts(any(), any(), any())
        } returns successfulResponse(MULTI_PRODUCT_RESPONSE)

        val result = dukanRepository.getShelfProducts(FAKE_SHELF_ID, page = 0, size = 10)

        assertEquals(2, result.items.size)
        assertEquals("Product 1", result.items[0].name)
        assertEquals("Product 2", result.items[1].name)
    }


    @Test
    fun `storeDukanDetails then getDukanDetails should return same dukan`() {
        dukanRepository.storeDukanDetails(FAKE_DUKAN)
        val result = dukanRepository.getDukanDetails()

        assertEquals(FAKE_DUKAN, result)
        assertEquals(FAKE_DUKAN.id, result.id)
        assertEquals(FAKE_DUKAN.name, result.name)
    }

    @Test
    fun `storeDukanDetails should overwrite previous dukan`() {
        dukanRepository.storeDukanDetails(FAKE_DUKAN)

        val newDukan = FAKE_DUKAN.copy(name = "Updated Dukan")
        dukanRepository.storeDukanDetails(newDukan)

        val result = dukanRepository.getDukanDetails()
        assertEquals("Updated Dukan", result.name)
    }

    @Test
    fun `clearDukanDetails should remove stored dukan`() {
        dukanRepository.storeDukanDetails(FAKE_DUKAN)
        dukanRepository.clearDukanDetails()

        assertFailsWith<IllegalArgumentException> {
            dukanRepository.getDukanDetails()
        }
    }

    @Test
    fun `getDukanDetails should throw exception when no dukan is stored`() {
        assertFailsWith<IllegalArgumentException> {
            dukanRepository.getDukanDetails()
        }
    }

    @Test
    fun `multiple store and clear operations should work correctly`() {
        dukanRepository.storeDukanDetails(FAKE_DUKAN)
        assertNotNull(dukanRepository.getDukanDetails())

        dukanRepository.clearDukanDetails()
        assertFailsWith<IllegalArgumentException> {
            dukanRepository.getDukanDetails()
        }

        dukanRepository.storeDukanDetails(FAKE_DUKAN)
        assertNotNull(dukanRepository.getDukanDetails())
    }

    @Test
    fun `activateDukan should successfully activate dukan`() = runTest {
        everySuspend {
            dukanApiService.activateDukan(any())
        } returns successfulResponse(Unit)

        dukanRepository.activateDukan(FAKE_UUID)
    }

    @Test
    fun `activateDukan should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.activateDukan(any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.activateDukan(FAKE_UUID)
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `deactivateDukan should successfully deactivate dukan with reason`() = runTest {
        everySuspend {
            dukanApiService.deactivateDukan(any(), any())
        } returns successfulResponse(Unit)

        dukanRepository.deactivateDukan(FAKE_UUID, "Violates policy")
    }

    @Test
    fun `deactivateDukan should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.deactivateDukan(any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.deactivateDukan(FAKE_UUID, "Test reason")
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    @Test
    fun `updateDukanStatus should successfully update dukan status`() = runTest {
        everySuspend {
            dukanApiService.updateDukanStatus(any(), any())
        } returns successfulResponse(Unit)

        dukanRepository.updateDukanStatus(
            dukanId = FAKE_UUID,
            status = Dukan.Status.APPROVED,
            message = "Status updated"
        )
    }

    @Test
    fun `updateDukanStatus should throw UnauthorizedException on 401 Unauthorized`() = runTest {
        everySuspend {
            dukanApiService.updateDukanStatus(any(), any())
        } returns unauthorizedResponse()

        val exception = assertFailsWith<UnauthorizedException> {
            dukanRepository.updateDukanStatus(
                dukanId = FAKE_UUID,
                status = Dukan.Status.REJECTED,
                message = "Does not meet requirements"
            )
        }

        assertTrue(exception.message?.contains("Unauthorized") == true)
    }

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        val FAKE_UUID: Uuid = Uuid.random()

        @OptIn(ExperimentalUuidApi::class)
        val FAKE_SHELF_ID: Uuid = Uuid.random()

        val DUKAN_DTO = DukanDto(
            id = FAKE_UUID.toString(),
            name = "Test Dukan",
            imageUrl = "",
            address = "Test Address",
            latitude = 30.0,
            longitude = 31.0,
            categories = emptyList(),
            createdAt = "2025-10-31T00:00:00",
            activationStatus = "ACTIVATED",
            status = "APPROVED"
        )

        val DUKAN_PAGED = DukanPagedResponse(
            totalPages = 1,
            totalElements = 1,
            size = 10,
            content = listOf(DUKAN_DTO),
            number = 0
        )

        val SHELF_DTO = ShelfDto(id = FAKE_UUID.toString(), title = "Top Shelf")

        val SHELF_PAGED = DukanPagedResponse(
            totalPages = 1,
            totalElements = 1,
            size = 10,
            content = listOf(SHELF_DTO),
            number = 0
        )

        val PRODUCT_DTO = ProductDto(
            id = FAKE_UUID.toString(),
            name = "Product 1",
            finalPrice = 100.0,
            basePrice = 80.0,
            description = "Good",
            imageUrls = listOf("https://img"),
            createdAt = "2025-10-31T00:00:00"
        )

        val PRODUCT_PAGED = DukanPagedResponse(
            totalPages = 1,
            totalElements = 1,
            size = 10,
            content = listOf(PRODUCT_DTO),
            number = 0
        )
        val FAKE_DUKAN = Dukan(
            id = FAKE_UUID,
            name = "Test Dukan",
            imageUrl = "",
            address = "Test Address",
            latitude = 30.0,
            longitude = 31.0,
            categories = emptyList(),
            createdAt = parseLocalDateTimeOrDefault("2025-10-31T00:00:00"),
            activationStatus = Dukan.ActivationStatus.ACTIVATED,
            status = Dukan.Status.APPROVED
        )
        val EmptyResultqueryParams = DukanQueryParams(
            searchInput = "",
            status = Dukan.Status.APPROVED,
            activationStatus = null,
            sortType = DukansSortType.CREATED_AT,
            sortDirection = SortDirection.DESC,
            page = 0,
            size = 10
        )
        val EMPTY_RESULT_RESPONSE = DukanPagedResponse<DukanDto>(
            totalPages = 0,
            totalElements = 0,
            size = 10,
            content = emptyList(),
            number = 0
        )
        val UNAUTHORIZED_EXCEPTION_QUERY_PARAMS = DukanQueryParams(
            searchInput = "",
            status = Dukan.Status.APPROVED,
            activationStatus = null,
            sortType = DukansSortType.CREATED_AT,
            sortDirection = SortDirection.DESC,
            page = 0,
            size = 10
        )
        val SUCCESS_STATE_QUERY_PARAMS = DukanQueryParams(
            searchInput = "test",
            status = Dukan.Status.APPROVED,
            activationStatus = Dukan.ActivationStatus.ACTIVATED,
            sortType = DukansSortType.CREATED_AT,
            sortDirection = SortDirection.DESC,
            page = 0,
            size = 10
        )
        val PRODUCT2 = ProductDto(
            id = Uuid.random().toString(),
            name = "Product 2",
            finalPrice = 200.0,
            basePrice = 150.0,
            description = "Better",
            imageUrls = listOf("https://img2"),
            createdAt = "2025-11-01T00:00:00"
        )
        val MULTI_PRODUCT_RESPONSE = DukanPagedResponse(
            totalPages = 1,
            totalElements = 2,
            size = 10,
            content = listOf(PRODUCT_DTO, PRODUCT2),
            number = 0
        )
        val EMPTY_PRODUCT_RESPONSE = DukanPagedResponse<ProductDto>(
            totalPages = 0,
            totalElements = 0,
            size = 10,
            content = emptyList(),
            number = 0
        )
        val MULTI_PAGE_RESPONSE = DukanPagedResponse(
            totalPages = 3,
            totalElements = 25,
            size = 10,
            content = listOf(SHELF_DTO),
            number = 1
        )

        private fun <T> successfulResponse(body: T): Response<T> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.OK
            }
            return Response.success(body = body, rawResponse = mockHttpResponse) as Response<T>
        }

        private fun <T> unauthorizedResponse(): Response<T> {
            val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
                everySuspend { status } returns HttpStatusCode.Unauthorized
            }
            return Response.error<T>(
                body = """{"message":"Unauthorized"}""",
                rawResponse = mockHttpResponse
            ) as Response<T>
        }
    }
}