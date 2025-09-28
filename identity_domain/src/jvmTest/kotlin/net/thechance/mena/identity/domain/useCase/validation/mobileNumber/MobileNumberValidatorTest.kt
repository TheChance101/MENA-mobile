package net.thechance.mena.identity.domain.useCase.validation.mobileNumber

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import org.junit.Test

internal class MobileNumberValidatorTest {
    private val mobileNumberValidator = MobileNumberValidator()

    @Test
    fun `should return true when use valid ALGERIA mobile number`() {
        val algeria = ValidMobileNumbersDummyData.ALGERIA

        val isValid = mobileNumberValidator.isValid(algeria.countryCode, algeria.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid BAHRAIN mobile number`() {
        val bahrain = ValidMobileNumbersDummyData.BAHRAIN

        val isValid = mobileNumberValidator.isValid(bahrain.countryCode, bahrain.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid EGYPT mobile number`() {
        val egypt = ValidMobileNumbersDummyData.EGYPT

        val isValid = mobileNumberValidator.isValid(egypt.countryCode, egypt.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid IRAN mobile number`() {
        val iran = ValidMobileNumbersDummyData.IRAN

        val isValid = mobileNumberValidator.isValid(iran.countryCode, iran.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid IRAQ mobile number`() {
        val iraq = ValidMobileNumbersDummyData.IRAQ

        val isValid = mobileNumberValidator.isValid(iraq.countryCode, iraq.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid JORDAN mobile number`() {
        val jordan = ValidMobileNumbersDummyData.JORDAN

        val isValid = mobileNumberValidator.isValid(jordan.countryCode, jordan.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid KUWAIT mobile number`() {
        val kuwait = ValidMobileNumbersDummyData.KUWAIT

        val isValid = mobileNumberValidator.isValid(kuwait.countryCode, kuwait.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid LEBANON mobile number`() {
        val lebanon = ValidMobileNumbersDummyData.LEBANON

        val isValid = mobileNumberValidator.isValid(lebanon.countryCode, lebanon.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid LIBYA mobile number`() {
        val libya = ValidMobileNumbersDummyData.LIBYA

        val isValid = mobileNumberValidator.isValid(libya.countryCode, libya.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid MOROCCO mobile number`() {
        val morocco = ValidMobileNumbersDummyData.MOROCCO

        val isValid = mobileNumberValidator.isValid(morocco.countryCode, morocco.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid OMAN mobile number`() {
        val oman = ValidMobileNumbersDummyData.OMAN

        val isValid = mobileNumberValidator.isValid(oman.countryCode, oman.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid PALESTINE mobile number`() {
        val palestine = ValidMobileNumbersDummyData.PALESTINE

        val isValid = mobileNumberValidator.isValid(palestine.countryCode, palestine.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid QATAR mobile number`() {
        val qatar = ValidMobileNumbersDummyData.QATAR

        val isValid = mobileNumberValidator.isValid(qatar.countryCode, qatar.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid SAUDI_ARABIA mobile number`() {
        val saudiArabia = ValidMobileNumbersDummyData.SAUDI_ARABIA

        val isValid = mobileNumberValidator.isValid(
            saudiArabia.countryCode,
            saudiArabia.mobileNumber
        )

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid SOMALIA mobile number`() {
        val somalia = ValidMobileNumbersDummyData.SOMALIA

        val isValid = mobileNumberValidator.isValid(somalia.countryCode, somalia.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid SUDAN mobile number`() {
        val sudan = ValidMobileNumbersDummyData.SUDAN

        val isValid = mobileNumberValidator.isValid(sudan.countryCode, sudan.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid SYRIA mobile number`() {
        val syria = ValidMobileNumbersDummyData.SYRIA

        val isValid = mobileNumberValidator.isValid(syria.countryCode, syria.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid TUNISIA mobile number`() {
        val tunisia = ValidMobileNumbersDummyData.TUNISIA

        val isValid = mobileNumberValidator.isValid(tunisia.countryCode, tunisia.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid UNITED_ARAB_EMIRATES mobile number`() {
        val unitedArabEmirates = ValidMobileNumbersDummyData.UNITED_ARAB_EMIRATES

        val isValid = mobileNumberValidator.isValid(
            unitedArabEmirates.countryCode,
            unitedArabEmirates.mobileNumber
        )

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when use valid YEMEN mobile number`() {
        val yemen = ValidMobileNumbersDummyData.YEMEN

        val isValid = mobileNumberValidator.isValid(yemen.countryCode, yemen.mobileNumber)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should throw country code not support yet when use invalid country code`() {
        val invalidCountryCode = "+000"
        val validMobileNumber = ValidMobileNumbersDummyData.EGYPT.mobileNumber

        assertFailure {
            mobileNumberValidator.isValid(invalidCountryCode, validMobileNumber)
        }.isInstanceOf(InvalidCountryCodeException::class)
    }

    @Test
    fun `should throw country code not support yet when use empty country code`() {
        val validMobileNumber = ValidMobileNumbersDummyData.EGYPT.mobileNumber

        assertFailure {
            mobileNumberValidator.isValid("", validMobileNumber)
        }.isInstanceOf(InvalidCountryCodeException::class)
    }

    @Test
    fun `should throw country code not support yet when use word country code`() {
        val validMobileNumber = ValidMobileNumbersDummyData.EGYPT.mobileNumber

        assertFailure {
            mobileNumberValidator.isValid("word", validMobileNumber)
        }.isInstanceOf(InvalidCountryCodeException::class)
    }

    @Test
    fun `should return false when country code is supported but with invalid mobile number`() {
        val validCountryCode = ValidMobileNumbersDummyData.EGYPT.countryCode
        val invalidMobileNumber = "0123414231"

        val isValid = mobileNumberValidator.isValid(validCountryCode, invalidMobileNumber)

        assertThat(isValid).isFalse()
    }

    @Test
    fun `should return false when country code is supported but with empty mobile number`() {
        val validCountryCode = ValidMobileNumbersDummyData.EGYPT.countryCode
        val mobileNumber = ""

        val isValid = mobileNumberValidator.isValid(validCountryCode, mobileNumber)

        assertThat(isValid).isFalse()
    }

    @Test
    fun `should return false when country code is supported but with word in mobile number`() {
        val validCountryCode = ValidMobileNumbersDummyData.EGYPT.countryCode
        val mobileNumber = "word"

        val isValid = mobileNumberValidator.isValid(validCountryCode, mobileNumber)

        assertThat(isValid).isFalse()
    }
}