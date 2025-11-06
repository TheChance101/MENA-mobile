package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.exception.FaithException
import org.junit.Test

import kotlin.test.*

class CalculateDistanceUseCaseTest {

    private val useCase = CalculateDistanceUseCase()

    @Test
    fun `invoke should return zero when coordinates are identical`() {
        val result = useCase(
            location1 = Location(latitude = 21.4225, longitude = 39.8262),
            location2 = Location(latitude = 21.4225, longitude = 39.8262),
        )
        assertEquals(0.0, result)
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                location1 = Location(latitude = 91.0, longitude = 0.0),
                location2 = Location(latitude = 0.0, longitude = 0.0),
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                location1 = Location(latitude = 0.0, longitude = 200.0),
                location2 = Location(latitude = 0.0, longitude = 0.0)
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                location1 = Location(latitude = 0.0, longitude = 0.0),
                location2 = Location(latitude = -100.0, longitude = 0.0)
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                location1 = Location(latitude = 0.0, longitude = 0.0),
                location2 = Location(latitude = 0.0, longitude = 200.0)
            )
        }
    }

    @Test
    fun `invoke should return correct distance between Mecca and Medina`() {
        // Given: Approximate coordinates of Mecca and Medina
        val meccaLat = 21.3891
        val meccaLon = 39.8579
        val medinaLat = 24.5247
        val medinaLon = 39.5692
        val meccaLocation = Location(latitude = meccaLat, longitude =  meccaLon)
        val medinaLocation = Location(latitude = medinaLat, longitude = medinaLon)

        // When
        val distance = useCase(location1 = meccaLocation, location2 = medinaLocation)

        // Then: Real-world distance ≈ 340 km (allow ±10 km tolerance)
        assertTrue(distance in 330.0..350.0, "Expected ≈340 km but got $distance")
    }

    @Test
    fun `invoke should return correct distance between New York and London`() {
        // Given
        val newYorkLat = 40.7128
        val newYorkLon = -74.0060
        val londonLat = 51.5074
        val londonLon = -0.1278
        val newYorkLocation = Location(latitude = newYorkLat, longitude =  newYorkLon)
        val londonLocation = Location(latitude = londonLat, longitude = londonLon)

        // When
        val distance = useCase(location1 = newYorkLocation, location2 = londonLocation)

        // Then
        assertTrue(distance in 5470.0..5670.0, "Expected ≈5570 km but got $distance")
    }

    @Test
    fun `invoke should handle valid boundary coordinates -90 latitude`() {
        val result = useCase(Location(-90.0, 0.0), location2 = Location(0.0, 0.0))
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +90 latitude`() {
        val result = useCase(location1 = Location(90.0, 0.0), location2 = Location( 0.0, 0.0))
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates -180 longitude`() {
        val result = useCase(Location(latitude = 0.0, longitude =  -180.0), location2 = Location(latitude = 0.0, longitude =  0.0))
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +180 longitude`() {
        val result = useCase(Location(latitude = 0.0, longitude = 180.0), location2 = Location( latitude = 0.0, longitude =  0.0))
        assertTrue(result >= 0.0)
    }

    @Test
    fun `toRadians should correctly convert degrees to radians`() {
        val method =
            CalculateDistanceUseCase::class.java.getDeclaredMethod("toRadians", Double::class.java)
        method.isAccessible = true

        val radians = method.invoke(useCase, 180.0) as Double
        assertEquals(Math.PI, radians, 1e-9)
    }

    @Test
    fun `isValidCoordinate should return true for valid range`() {
        val method = CalculateDistanceUseCase::class.java.getDeclaredMethod(
            "isValidCoordinate",
            Double::class.java,
            Double::class.java
        )
        method.isAccessible = true

        val result = method.invoke(useCase, 45.0, 90.0) as Boolean
        assertTrue(result)
    }

    @Test
    fun `isValidCoordinate should return false for invalid latitude`() {
        val method = CalculateDistanceUseCase::class.java.getDeclaredMethod(
            "isValidCoordinate",
            Double::class.java,
            Double::class.java
        )
        method.isAccessible = true

        val result = method.invoke(useCase, -91.0, 0.0) as Boolean
        assertFalse(result)
    }

    @Test
    fun `isValidCoordinate should return false for invalid longitude`() {
        val method = CalculateDistanceUseCase::class.java.getDeclaredMethod(
            "isValidCoordinate",
            Double::class.java,
            Double::class.java
        )
        method.isAccessible = true

        val result = method.invoke(useCase, 0.0, 181.0) as Boolean
        assertFalse(result)
    }

    @Test
    fun `invoke should coerce negative values to zero`() {
        val result = useCase(Location(0.0, 0.0), location2 = Location( 0.0, 0.0))
        assertEquals(0.0, result)
    }
}
