package net.thechance.mena.dukan.presentation.viewModel.createDukan

import dev.mokkery.MockMode
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateDukanViewModelTest {

    private val locationRepository = mock<LocationRepository>(mode = MockMode.autofill)
    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private lateinit var createDukanViewModel: CreateDukanViewModel

    @BeforeTest
    fun setup() {
        createDukanViewModel = CreateDukanViewModel(dukanRepository, locationRepository)
    }

    @Test
    fun `onAddressChanged should update address state`() = runTest {
        val address = "This new address"

        createDukanViewModel.onAddressChanged(address)
        val newAddress = createDukanViewModel.state.value.address
        assertEquals(newAddress, address)
    }
}