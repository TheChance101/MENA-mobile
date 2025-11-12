package net.thechance.mena.identity.domain.useCase.validation.mobileNumber

import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException

class MobileNumberValidator {
    fun isValid(countryCode: String, number: String): Boolean {
        val country = getCountry(countryCode)
        return isMobileNumberValid(number, country.regexPattern)
    }

    private fun getCountry(countryCode: String): Country {
        return Country.entries
            .firstOrNull { it.code == countryCode }
            ?: throw InvalidCountryCodeException(countryCode)
    }

    private fun isMobileNumberValid(
        number: String,
        regexPattern: Regex
    ) = number.matches(regexPattern)

    private enum class Country(val code: String, val regexPattern: Regex) {
        ALGERIA("+213", "^(0?)([567])\\d{8}$".toRegex()),
        BAHRAIN("+973", "^([36])\\d{7}$".toRegex()),
        EGYPT("+20", "^(0)?1[0125]\\d{8}$".toRegex()),
        IRAN("+98", "^(0)?9\\d{9}$".toRegex()),
        IRAQ("+964", "^(0)?7[0-9]\\d{8}$".toRegex()),
        JORDAN("+962", "^(0)?7[789]\\d{7}$".toRegex()),
        KUWAIT("+965", "^([569]\\d{7}|41\\d{6})$".toRegex()),
        LEBANON("+961", "^((3|81)\\d{6}|7\\d{7})$".toRegex()),
        LIBYA("+218", "^(0)?(9[1-6]\\d{7}|[1-8]\\d{7,9})$".toRegex()),
        MOROCCO("+212", "^(0)?[5-7]\\d{8}$".toRegex()),
        OMAN("+968", "^(9[1-9])\\d{6}$".toRegex()),
        PALESTINE("+970", "^(0)?5[69]\\d{7}$".toRegex()),
        QATAR("+974", "^[3567]\\d{7}$".toRegex()),
        SAUDI_ARABIA("+966", "^(0)?5\\d{8}$".toRegex()),
        SOMALIA("+252", "^(0)?((6[0-9])\\d{7}|(7[1-9])\\d{7})$".toRegex()),
        SUDAN("+249", "^(0)?(9[0125679])\\d{7}$".toRegex()),
        SYRIA("+963", "^(0)?9\\d{8}$".toRegex()),
        TUNISIA("+216", "^[2459]\\d{7}$".toRegex()),
        UNITED_ARAB_EMIRATES("+971", "^(0)?5[024568]\\d{7}$".toRegex()),
        YEMEN("+967", "^((7|0?7)[0137]\\d{7}|((\\+|00)967|0)[1-7]\\d{6})$".toRegex())
    }
}