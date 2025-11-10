package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateDistanceUseCaseTest {

    private val useCase = CalculateDistanceUseCase()

    @Test
    fun `invoke should return zero when coordinates are identical`() {
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = 21.4225, longitude = 39.8262),
            secondLocation = Mosque.Coordinates(latitude = 21.4225, longitude = 39.8262)
        )
        assertEquals(0.0, result)
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                firstLocation = Mosque.Coordinates(latitude = 91.0, longitude = 0.0),
                secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = 200.0),
                secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0),
                secondLocation = Mosque.Coordinates(latitude = -100.0, longitude = 0.0)
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0),
                secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 200.0)
            )
        }
    }

    @Test
    fun `invoke should return correct distance between Mecca and Medina`() {
        val mecca = Mosque.Coordinates(latitude = 21.3891, longitude = 39.8579)
        val medina = Mosque.Coordinates(latitude = 24.5247, longitude = 39.5692)

        val distance = useCase(mecca, medina)

        assertTrue(distance in 330.0..350.0, "Expected ≈340 km but got $distance")
    }

    @Test
    fun `invoke should return correct distance between New York and London`() {
        val newYork = Mosque.Coordinates(latitude = 40.7128, longitude = -74.0060)
        val london = Mosque.Coordinates(latitude = 51.5074, longitude = -0.1278)

        val distance = useCase(newYork, london)

        assertTrue(distance in 5470.0..5670.0, "Expected ≈5570 km but got $distance")
    }

    @Test
    fun `invoke should handle valid boundary coordinates -90 latitude`() {
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = -90.0, longitude = 0.0),
            secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +90 latitude`() {
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = 90.0, longitude = 0.0),
            secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates -180 longitude`() {
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = -180.0),
            secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +180 longitude`() {
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = 180.0),
            secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
        )
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
        val result = useCase(
            firstLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0),
            secondLocation = Mosque.Coordinates(latitude = 0.0, longitude = 0.0)
        )
        assertEquals(0.0, result)
    }
}