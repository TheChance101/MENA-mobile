package net.thechance.mena.identity.domain.useCase.validation.mobileNumber

internal enum class ValidMobileNumbersDummyData(
    val countryCode: String,
    val mobileNumber: String
) {
    ALGERIA("+213", "799066365"),
    BAHRAIN("+973", "38855527"),
    EGYPT("+20", "01283393335"),
    IRAN("+98", "9104079193"),
    IRAQ("+964", "7701234567"),
    JORDAN("+962", "771234567"),
    KUWAIT("+965", "57341506"),
    LEBANON("+961", "76555211"),
    LIBYA("+218", "40946386"),
    MOROCCO("+212", "613509840"),
    OMAN("+968", "91555054"),
    PALESTINE("+970", "595550922"),
    QATAR("+974", "55555791"),
    SAUDI_ARABIA("+966", "565558574"),
    SOMALIA("+252", "611234567"),
    SUDAN("+249", "955590790"),
    SYRIA("+963", "965550163"),
    TUNISIA("+216", "95557823"),
    UNITED_ARAB_EMIRATES("+971", "555555888"),
    YEMEN("+967", "705558178")
}