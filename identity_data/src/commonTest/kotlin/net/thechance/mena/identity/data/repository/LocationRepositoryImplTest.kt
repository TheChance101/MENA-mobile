package net.thechance.mena.identity.data.repository

import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.LocationRepositoryImpl
import net.thechance.mena.identity.domain.entity.Coordinates

class LocationRepositoryImplTest {

    private val geocoder = mock<GeocoderWrapper>()
    private val locationRepositoryImpl: LocationRepositoryImpl = LocationRepositoryImpl(geocoder)

    @Test
    fun `getLocationName should return formatted address when the place is not valid`() =
        runTest {
            val domainCoordinates = Coordinates(28.0, 29.0)
            coEvery { geocoder.placeOrNull(any()) } returns Place(
                subAdministrativeArea = "Basra",
                administrativeArea = "Basra Governorate",
                country = "Basra",
                isoCountryCode = null,
                coordinates = Coordinates(domainCoordinates.latitude, domainCoordinates.longitude),
                name = "",
                street = "",
                postalCode = "",
                locality = "",
                subLocality = "",
                thoroughfare = "",
                subThoroughfare = ""
            )

            val result = locationRepositoryImpl.getLocationName(domainCoordinates)

            assertEquals("Basra, Basra Governorate, Basra", result)
        }

    @Test
    fun `getLocationName should return empty address when the place is null`() = runTest {
        val domainCoordinates = Coordinates(28.0, 29.0)
        coEvery { geocoder.placeOrNull(any()) } returns null

        val result = locationRepositoryImpl.getLocationName(domainCoordinates)

        assertEquals("", result)
    }
}