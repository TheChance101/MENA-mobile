package net.thechance.mena.identity.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileLocationRepositoryImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import net.thechance.mena.identity.domain.entity.Coordinates as DomainCoordinates

class LocationRepositoryImplTest {

    private val geocoder = mockk<GeocoderWrapper>()
    private val locationRepositoryImpl: MobileLocationRepositoryImpl =
        MobileLocationRepositoryImpl(geocoder)

    @Test
    fun `getLocationName should return formatted address when the place is not valid`() =
        runTest {
            val domainCoordinates = DomainCoordinates(LATITUDE, LONGITUDE)
            coEvery { geocoder.placeOrNull(any()) } returns place

            val result = locationRepositoryImpl.getLocationName(domainCoordinates)

            assertEquals("Basra, Basra Governorate, Basra", result)
        }

    @Test
    fun `getLocationName should return empty address when the place is null`() = runTest {
        val domainCoordinates = DomainCoordinates(LATITUDE, LONGITUDE)
        coEvery { geocoder.placeOrNull(any()) } returns null

        val result = locationRepositoryImpl.getLocationName(domainCoordinates)

        assertEquals("", result)
    }

    private companion object{
        const val LATITUDE = 28.0
        const val LONGITUDE = 29.0

        val place = Place(
            subAdministrativeArea = "Basra",
            administrativeArea = "Basra Governorate",
            country = "Basra",
            isoCountryCode = null,
            coordinates = Coordinates(LATITUDE, LONGITUDE),
            name = "",
            street = "",
            postalCode = "",
            locality = "",
            subLocality = "",
            thoroughfare = "",
            subThoroughfare = "")
    }
}