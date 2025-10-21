package net.thechance.mena.dukan.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.util.wrapper.GeocoderWrapper
import net.thechance.mena.dukan.domain.entity.Dukan
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationRepositoryImplTest {

    private val geocoder = mock<GeocoderWrapper>(mode = MockMode.autofill)
    private lateinit var locationRepositoryImpl: LocationRepositoryImpl

    @BeforeTest
    fun setup() {
        locationRepositoryImpl = LocationRepositoryImpl(geocoder)
    }

    @Test
    fun `getCurrentLocationName SHOULD return formatted address WHEN the place is not valid`() =
        runTest {
            // Given
            val coordinates = Dukan.Coordinates(28.0, 29.0)
            everySuspend { geocoder.placeOrNull(any()) } returns Place(
                subAdministrativeArea = "Cairo",
                administrativeArea = "Cairo Governorate",
                country = "Egypt",
                isoCountryCode = null,
                coordinates = Coordinates(coordinates.latitude, coordinates.longitude),
                name = "",
                street = "",
                postalCode = "",
                locality = "",
                subLocality = "",
                thoroughfare = "",
                subThoroughfare = ""
            )

            // When
            val result = locationRepositoryImpl.getCurrentLocationName(coordinates)

            // Then
            assertEquals("Cairo, Cairo Governorate, Egypt", result)
        }

    @Test
    fun `getCurrentLocationName SHOULD return empty address WHEN the place is null`() = runTest {
        // Given
        val coordinates = Dukan.Coordinates(28.0, 29.0)
        everySuspend { geocoder.placeOrNull(any()) } returns null

        // When
        val result = locationRepositoryImpl.getCurrentLocationName(coordinates)

        // Then
        assertEquals("", result)
    }
}