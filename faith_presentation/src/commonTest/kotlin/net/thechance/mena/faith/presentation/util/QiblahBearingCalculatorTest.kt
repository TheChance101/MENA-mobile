package net.thechance.mena.faith.presentation.util

import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QiblahBearingCalculatorTest {

    private val calculator: QiblahBearingCalculator = QiblahBearingCalculator()

    @Test
    fun `calculateQiblahAngle should return zero angle when User at Kaaba exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = calculator.kaabaLatitude,
            userLongitude = calculator.kaabaLongitude
        )
        assertEquals(0.0, result)
    }

    @Test
    fun `calculateQiblahAngle should return 153,30 angle when User at Gaza exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = 31.5017,
            userLongitude = 34.4668
        )
        assertEquals(round(153.3), result)
    }

    @Test
    fun `calculateQiblahAngle should return 136,14 angle when User at Cairo exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = 30.0444,
            userLongitude = 31.2357
        )
        assertEquals(round(136.14), result)
    }

    @Test
    fun `calculateQiblahAngle should return 199,80 angle when User at Baghdad exact location`() {
        val result = calculator.calculateQiblahAngle(
            userLatitude = 33.3128,
            userLongitude = 44.3615
        )
        assertEquals(round(199.80), result)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is -90°`() {
        val result = calculator.calculateQiblahAngle(-90.0, 0.0)
        assertTrue(result in 0.0..360.0)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when latitude is +90°`() {
        val result = calculator.calculateQiblahAngle(90.0, 0.0)
        assertTrue(result in 0.0..360.0)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is +100°`() {
        assertFailsWith<IllegalArgumentException>(
            "Latitude > 90 should throw exception"
        ) {
            calculator.calculateQiblahAngle(100.0, 0.0)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when latitude is -91°`() {
        assertFailsWith<IllegalArgumentException>(
            "Latitude < -90 should throw exception"
        ) {
            calculator.calculateQiblahAngle(-91.0, 0.0)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is -180°`() {
        val result = calculator.calculateQiblahAngle(0.0, -180.0)
        assertTrue(result in 0.0..360.0)
    }

    @Test
    fun `calculateQiblahAngle Maximum valid when longitude is +180°`() {
        val result = calculator.calculateQiblahAngle(0.0, 180.0)
        assertTrue(result in 0.0..360.0)
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is +200°`() {
        assertFailsWith<IllegalArgumentException>(
            "longitude > 180 should throw exception"
        ) {
            calculator.calculateQiblahAngle(0.0, 200.0)
        }
    }

    @Test
    fun `calculateQiblahAngle Maximum invalid when longitude is -320°`() {
        assertFailsWith<IllegalArgumentException>(
            "longitude < -180 should throw exception"
        ) {
            calculator.calculateQiblahAngle(0.0, -320.0)
        }
    }

    @Test
    fun `calculateQiblahAngle should angle between 0 to 360`() {
        val result = calculator.calculateQiblahAngle(31.0, 39.0)
        assertTrue(result in 0.0..360.0)
    }
}