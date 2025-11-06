package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Location
import kotlin.math.abs
import kotlin.test.*

class CalculateDistanceBetweenLocationsUseCaseTest {

    private lateinit var useCase: CalculateDistanceBetweenLocationsUseCase

    @BeforeTest
    fun setup() {
        useCase = CalculateDistanceBetweenLocationsUseCase()
    }

    @Test
    fun `should calculate zero distance for same coordinates`() {
        val location = Location(latitude = 30.0, longitude = 31.0)

        val result = useCase.execute(location, location)

        assertEquals(0.0, result, "Distance between identical locations should be 0")
    }

    @Test
    fun `should calculate correct distance between Cairo and Mecca`() {
        val cairo = Location(latitude = 30.0444, longitude = 31.2357)
        val mecca = Location(latitude = 21.3891, longitude = 39.8579)

        val result = useCase.execute(cairo, mecca)

        assertTrue(
            abs(result - 1300.0) < 50.0,
            "Expected distance between Cairo and Mecca to be around 1300km but got $result"
        )
    }

    @Test
    fun `should throw exception for invalid latitude`() {
        val invalidLocation = Location(latitude = 100.0, longitude = 31.0)
        val validLocation = Location(latitude = 30.0, longitude = 31.0)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(invalidLocation, validLocation)
        }
    }

    @Test
    fun `should throw exception for invalid longitude`() {
        val invalidLocation = Location(latitude = 30.0, longitude = 200.0)
        val validLocation = Location(latitude = 30.0, longitude = 31.0)

        assertFailsWith<IllegalArgumentException> {
            useCase.execute(validLocation, invalidLocation)
        }
    }

    @Test
    fun `distance should be symmetric between two locations`() {
        // Given
        val loc1 = Location(latitude = 25.0, longitude = 30.0)
        val loc2 = Location(latitude = 26.0, longitude = 31.0)

        val distance1 = useCase.execute(loc1, loc2)
        val distance2 = useCase.execute(loc2, loc1)

        assertEquals(distance1, distance2, 0.0001, "Distance should be symmetric between two points")
    }
}
