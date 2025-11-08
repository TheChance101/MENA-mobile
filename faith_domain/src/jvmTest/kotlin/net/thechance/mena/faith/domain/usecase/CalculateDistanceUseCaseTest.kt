package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CalculateDistanceUseCaseTest {

    private val useCase = CalculateDistanceUseCase()

    @Test
    fun `invoke should return zero when coordinates are identical`() {
        val result = useCase(
            address1 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home,
                latitude = 21.4225, longitude = 39.8262,
            ),
            address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 21.4225, longitude = 39.8262
            ),
        )
        assertEquals(0.0, result)
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                address1 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 91.0, longitude = 0.0
                ),
                address2 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
                ),
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when first coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                address1 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 200.0
                ),
                address2 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
                )
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate latitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                address1 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
                ),
                address2 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = -100.0, longitude = 0.0
                )
            )
        }
    }

    @Test
    fun `invoke should throw InvalidCoordinates when second coordinate longitude is invalid`() {
        assertFailsWith<FaithException.InvalidCoordinates> {
            useCase(
                address1 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
                ),
                address2 = Address(
                    id = Uuid.random(),
                    addressLine = "Baghdad, Iraq",
                    addressType = AddressType.Home, latitude = 0.0, longitude = 200.0
                )
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
        val meccaAddress = Address(
            id = Uuid.random(),
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home, latitude = meccaLat, longitude = meccaLon
        )
        val medinaAddress = Address(
            id = Uuid.random(),
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home, latitude = medinaLat, longitude = medinaLon
        )

        // When
        val distance = useCase(address1 = meccaAddress, address2 = medinaAddress)

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
        val newYorkAddress = Address(
            id = Uuid.random(),
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home, latitude = newYorkLat, longitude = newYorkLon
        )
        val londonAddress = Address(
            id = Uuid.random(),
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home, latitude = londonLat, longitude = londonLon
        )

        // When
        val distance = useCase(address1 = newYorkAddress, address2 = londonAddress)

        // Then
        assertTrue(distance in 5470.0..5670.0, "Expected ≈5570 km but got $distance")
    }

    @Test
    fun `invoke should handle valid boundary coordinates -90 latitude`() {
        val result = useCase(
            Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = -90.0, longitude = 0.0
            ), address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            )
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +90 latitude`() {
        val result = useCase(
            address1 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 90.0, longitude = 0.0
            ), address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            )
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates -180 longitude`() {
        val result = useCase(
            Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = -180.0
            ), address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            )
        )
        assertTrue(result >= 0.0)
    }

    @Test
    fun `invoke should handle valid boundary coordinates +180 longitude`() {
        val result = useCase(
            Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 180.0
            ), address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            )
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
            Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            ), address2 = Address(
                id = Uuid.random(),
                addressLine = "Baghdad, Iraq",
                addressType = AddressType.Home, latitude = 0.0, longitude = 0.0
            )
        )
        assertEquals(0.0, result)
    }

}
