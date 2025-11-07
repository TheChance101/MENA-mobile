package net.thechance.mena.identity.domain.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.util.createAddress
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class LocationServiceTest {

    private val addressRepository: AddressesRepository = mockk()
    private val locationService = LocationService(addressRepository)
    private val expectedAddresses = listOf(
        createAddress(addressType = AddressType.Home),
        createAddress(addressType = AddressType.Office),
        createAddress(addressType = AddressType.Other("Other"))
    )

    @Test
    fun `getUserAddresses() should return user addresses`() = runTest {
        coEvery { addressRepository.getUserAddresses() } returns expectedAddresses

        val actualAddresses = locationService.getUserAddresses()

        assertThat(actualAddresses).isEqualTo(expectedAddresses)
    }

    @Test
    fun `getActiveAddress() should return active address`() = runTest {
        val expectedActiveAddress = expectedAddresses.first()
        coEvery { addressRepository.getActiveAddress() } returns expectedActiveAddress

        val actualActiveAddress = locationService.getActiveAddress()

        assertThat(actualActiveAddress).isEqualTo(expectedActiveAddress)
    }
}