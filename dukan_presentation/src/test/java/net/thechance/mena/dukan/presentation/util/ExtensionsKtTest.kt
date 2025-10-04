package net.thechance.mena.dukan.presentation.util

import junit.framework.TestCase.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsKtTest {

    @Test
    fun `rounded should round to two decimals`() {
        assertEquals(3.14, 3.14159.rounded())
        assertEquals(2.72, 2.71828.rounded())
        assertEquals(1.0, 1.0.rounded())
        assertEquals(-1.23, (-1.234).rounded())
    }

    @Test
    fun `rounded should handle rounding up correctly`() {
        assertEquals(2.46, 2.455.rounded())
    }



    @Test
    fun `toFileName should generate file name with timestamp and suffix`() {
        val bytes = byteArrayOf(1, 2, 3)
        val fileName = bytes.toFileName()

        assertTrue(fileName.endsWith("+product_image"))

        val timestampPart = fileName.removeSuffix("+product_image")
        assertTrue(timestampPart.toLongOrNull() != null, "Timestamp part is not numeric")
    }

}