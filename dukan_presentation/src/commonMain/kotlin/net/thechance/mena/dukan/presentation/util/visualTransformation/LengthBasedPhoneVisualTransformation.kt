package net.thechance.mena.dukan.presentation.util.visualTransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.text.iterator

class LengthBasedPhoneVisualTransformation(
    private val masks: Map<Int, String>
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val transformed = getTransformed(digits)

        val digitPositions = mutableListOf<Int>()
        var dIndex = 0
        transformed.forEachIndexed { tIndex, c ->
            if (c.isDigit()) {
                if (dIndex >= digitPositions.size)
                    digitPositions.add(tIndex)
                dIndex++
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val o = offset.coerceIn(0, digitPositions.size)
                return if (o == digitPositions.size) transformed.length else digitPositions[o]
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset >= transformed.length) return digitPositions.size
                return transformed.take(offset).count { it.isDigit() }
            }
        }

        return TransformedText(androidx.compose.ui.text.AnnotatedString(transformed), offsetMapping)
    }

    private fun getTransformed(digits: String): String {
        val mask = masks[digits.length]
        if (mask == null) {
            return digits
        }
        val out = StringBuilder()
        var digitIndex = 0
        for (m in mask) {
            if (m == '#') {
                if (digitIndex < digits.length) {
                    out.append(digits[digitIndex++])
                }
            } else {
                if (digitIndex < digits.length) {
                    out.append(m)
                }
            }
        }
        return out.toString()
    }
    companion object {
         val phoneNumberMasks = mapOf(
            8 to "##\u00A0###\u00A0###",
            9 to "###\u00A0###\u00A0###",
            10 to "##\u00A0####\u00A0####",
            11 to "###\u00A0####\u00A0####",
            12 to "##\u00A0###\u00A0###\u00A0####",
            13 to "###\u00A0###\u00A0####\u00A0###",
            14 to "###\u00A0###\u00A0####\u00A0####",
            15 to "####\u00A0###\u00A0####\u00A0####",
        )
    }
}