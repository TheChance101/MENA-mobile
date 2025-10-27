package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QiblahBearingCalculatorUseCaseTest {

    private val calculator: QiblahBearingCalculatorUseCase = QiblahBearingCalculatorUseCase()

    @Test
    fun `calculateQiblahAngle should return zero angle when User at Kaaba exact location`() {
        val result = calculator.calculateQiblahAngle(KAABA_LOCATION)
        assertEquals(RANGE_ANGLE_START, result)
    }

    @Test
    fun `calculateQiblahAngle should return 153,30 angle when User at Gaza exact location`() {
        val result = calculator.calculateQiblahAngle(GAZA_LOCATION)
        assertEquals(round(GAZA_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle should return 136,14 angle when User at Cairo exact location`() {
        val result = calculator.calculateQiblahAngle(CAIRO_LOCATION)
        assertEquals(round(CAIRO_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle should return 199,80 angle when User at Baghdad exact location`() {
        val result = calculator.calculateQiblahAngle(BAGHDAD_LOCATION)
        assertEquals(round(BAGHDAD_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is -90`() {
        val result = calculator.calculateQiblahAngle(MIN_LATITUDE_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is +90`() {
        val result = calculator.calculateQiblahAngle(MAX_LATITUDE_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is +100`() {
        assertFailsWith<FaithException.InvalidLatitudeException> {
            calculator.calculateQiblahAngle(INVALID_MAX_LATITUDE_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is -91`() {
        assertFailsWith<FaithException.InvalidLatitudeException> {
            calculator.calculateQiblahAngle(INVALID_MIN_LATITUDE_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is -180`() {
        val result = calculator.calculateQiblahAngle(MIN_LONGITUDE_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is +180`() {
        val result = calculator.calculateQiblahAngle(MAX_LONGITUDE_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is +200`() {
        assertFailsWith<FaithException.InvalidLongitudeException> {
            calculator.calculateQiblahAngle(INVALID_MAX_LONGITUDE_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is -320`() {
        assertFailsWith<FaithException.InvalidLongitudeException> {
            calculator.calculateQiblahAngle(INVALID_MIN_LONGITUDE_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle should angle between 0 to 360`() {
        val result = calculator.calculateQiblahAngle(GAZA_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `getShortestAngleDifference should return zero when from and to are same`() {
        // Given
        val from = ZERO_ANGLE
        val to = ZERO_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(ZERO_ANGLE, result)
    }

    @Test
    fun `getShortestAngleDifference should return positive diff when to is greater than from`() {
        // Given
        val from = SMALL_ANGLE
        val to = MEDIUM_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(EXPECTED_DIFF_40, result)
    }

    @Test
    fun `getShortestAngleDifference should return negative diff when to is less than from`() {
        // Given
        val from = MEDIUM_ANGLE
        val to = SMALL_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(EXPECTED_NEGATIVE_40, result)
    }

    @Test
    fun `getShortestAngleDifference should return shortest path when crossing 0 degrees`() {
        // Given
        val from = LARGE_ANGLE
        val to = SMALL_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(EXPECTED_DIFF_20, result)
    }

    @Test
    fun `getShortestAngleDifference should return shortest path when crossing 360 degrees`() {
        // Given
        val from = SMALL_ANGLE
        val to = LARGE_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(EXPECTED_NEGATIVE_20, result)
    }

    @Test
    fun `getShortestAngleDifference should handle 180 degree difference`() {
        // Given
        val from = ZERO_ANGLE
        val to = STRAIGHT_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertTrue(result == STRAIGHT_ANGLE || result == -STRAIGHT_ANGLE)
    }

    @Test
    fun `getShortestAngleDifference should handle full circle rotation`() {
        // Given
        val from = ZERO_ANGLE
        val to = FULL_CIRCLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(ZERO_ANGLE, result)
    }

    @Test
    fun `getShortestAngleDifference should handle negative angles`() {
        // Given
        val from = NEGATIVE_ANGLE
        val to = SMALL_ANGLE

        // When
        val result = calculator.getShortestAngleDifference(from, to)

        // Then
        assertEquals(EXPECTED_DIFF_20, result)
    }

    private companion object {
        val KAABA_LOCATION = demoLocation(latitude = 21.4225, longitude = 39.8262)
        val GAZA_LOCATION = demoLocation(latitude = 31.5017, longitude = 34.4668)
        const val GAZA_QIBLA_ANGLE = 153.3
        val CAIRO_LOCATION = demoLocation(latitude = 30.0444, longitude = 31.2357)
        const val CAIRO_QIBLA_ANGLE = 136.14
        val BAGHDAD_LOCATION = demoLocation(latitude = 33.3128, longitude = 44.3615)
        const val BAGHDAD_QIBLA_ANGLE = 199.80

        val MIN_LATITUDE_LOCATION = demoLocation(latitude = -90.0)
        val MAX_LATITUDE_LOCATION = demoLocation(latitude = 90.0)
        val INVALID_MIN_LATITUDE_LOCATION =
            demoLocation(latitude = -91.0)
        val INVALID_MAX_LATITUDE_LOCATION =
            demoLocation(latitude = 100.0)
        val MIN_LONGITUDE_LOCATION = demoLocation(longitude = -180.0)
        val MAX_LONGITUDE_LOCATION = demoLocation(longitude = 180.0)
        val INVALID_MIN_LONGITUDE_LOCATION =
            demoLocation(longitude = -320.0)
        val INVALID_MAX_LONGITUDE_LOCATION =
            demoLocation(longitude = 200.0)
        const val ZERO_LOCATION = 0.0
        const val RANGE_ANGLE_START = 0.0
        const val RANGE_ANGLE_END = 360.0
        const val ZERO_ANGLE = 0f
        const val SMALL_ANGLE = 10f
        const val MEDIUM_ANGLE = 50f
        const val LARGE_ANGLE = 350f
        const val STRAIGHT_ANGLE = 180f
        const val FULL_CIRCLE = 360f
        const val NEGATIVE_ANGLE = -10f
        const val EXPECTED_DIFF_40 = 40f
        const val EXPECTED_DIFF_20 = 20f
        const val EXPECTED_NEGATIVE_40 = -40f
        const val EXPECTED_NEGATIVE_20 = -20f

        @OptIn(ExperimentalUuidApi::class)
        fun demoLocation(latitude: Double = ZERO_LOCATION, longitude: Double = ZERO_LOCATION) =
            Address(
                id = Uuid.random(),
                latitude = latitude,
                longitude = longitude,
                addressLine = "",
                addressType = AddressType.Home
            )
    }
}