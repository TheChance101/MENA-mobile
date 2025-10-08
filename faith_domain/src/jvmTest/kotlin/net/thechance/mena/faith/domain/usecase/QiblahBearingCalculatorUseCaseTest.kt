package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QiblahBearingCalculatorUseCaseTest {

    private val calculator: QiblahBearingCalculatorUseCase = QiblahBearingCalculatorUseCase()

    @Test
    fun `calculateQiblahAngle should return zero angle when User at Kaaba exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = KAABA_LATITUDE,
            userLongitude = KAABA_LONGITUDE
        )
        assertEquals(RANGE_ANGLE_START, result)
    }

    @Test
    fun `calculateQiblahAngle should return 153,30 angle when User at Gaza exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = GAZA_LATITUDE,
            userLongitude = GAZA_LONGITUDE
        )
        assertEquals(round(GAZA_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle should return 136,14 angle when User at Cairo exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = CAIRO_LATITUDE,
            userLongitude = CAIRO_LONGITUDE
        )
        assertEquals(round(CAIRO_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle should return 199,80 angle when User at Baghdad exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = BAGHDAD_LATITUDE,
            userLongitude = BAGHDAD_LONGITUDE
        )
        assertEquals(round(BAGHDAD_QIBLA_ANGLE), result)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is -90`() {
        val result = calculator.calculateQiblahAngle(MIN_LATITUDE, ZERO_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is +90`() {
        val result = calculator.calculateQiblahAngle(MAX_LATITUDE, ZERO_LOCATION)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is +100`() {
        assertFailsWith<FaithException.InvalidLatitudeException> {
            calculator.calculateQiblahAngle(INVALID_MAX_LATITUDE, ZERO_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is -91`() {
        assertFailsWith<FaithException.InvalidLatitudeException> {
            calculator.calculateQiblahAngle(INVALID_MIN_LATITUDE, ZERO_LOCATION)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is -180`() {
        val result = calculator.calculateQiblahAngle(ZERO_LOCATION, MIN_LONGITUDE)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is +180`() {
        val result = calculator.calculateQiblahAngle(ZERO_LOCATION, MAX_LONGITUDE)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is +200`() {
        assertFailsWith<FaithException.InvalidLongitudeException> {
            calculator.calculateQiblahAngle(ZERO_LOCATION, INVALID_MAX_LONGITUDE)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is -320`() {
        assertFailsWith<FaithException.InvalidLongitudeException> {
            calculator.calculateQiblahAngle(ZERO_LOCATION, INVALID_MIN_LONGITUDE)
        }
    }

    @Test
    fun `calculateQiblahAngle should angle between 0 to 360`() {
        val result = calculator.calculateQiblahAngle(GAZA_LATITUDE, GAZA_LONGITUDE)
        assertTrue(result in RANGE_ANGLE_START..RANGE_ANGLE_END)
    }

    private companion object {
        const val KAABA_LATITUDE = 21.4225
        const val KAABA_LONGITUDE = 39.8262

        const val GAZA_LATITUDE = 31.5017
        const val GAZA_LONGITUDE = 34.4668
        const val GAZA_QIBLA_ANGLE = 153.3

        const val CAIRO_LATITUDE = 30.0444
        const val CAIRO_LONGITUDE = 31.2357
        const val CAIRO_QIBLA_ANGLE = 136.14

        const val BAGHDAD_LATITUDE = 33.3128
        const val BAGHDAD_LONGITUDE = 44.3615
        const val BAGHDAD_QIBLA_ANGLE = 199.80

        const val RANGE_ANGLE_START = 0.0
        const val RANGE_ANGLE_END = 360.0

        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        const val ZERO_LOCATION = 0.0

        const val INVALID_MAX_LATITUDE = 100.0
        const val INVALID_MIN_LATITUDE = -91.0
        const val INVALID_MAX_LONGITUDE = 200.0
        const val INVALID_MIN_LONGITUDE = -320.0
    }
}