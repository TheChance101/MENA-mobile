package net.thechance.mena.identity.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileLocationRepositoryImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import net.thechance.mena.identity.domain.entity.Coordinates as DomainCoordinates

class LocationRepositoryImplTest {

    private val geocoder = mock<GeocoderWrapper>()
    private val locationRepositoryImpl: MobileLocationRepositoryImpl = MobileLocationRepositoryImpl(geocoder)

    @Test
    fun `getLocationName should return formatted address when the place is not valid`() =
        runTest {
            val domainCoordinates = DomainCoordinates(28.0, 29.0)
            everySuspend { geocoder.placeOrNull(any()) } returns Place(
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
        val domainCoordinates = DomainCoordinates(28.0, 29.0)
        everySuspend { geocoder.placeOrNull(any()) } returns null

        val result = locationRepositoryImpl.getLocationName(domainCoordinates)

        assertEquals("", result)
    }
}