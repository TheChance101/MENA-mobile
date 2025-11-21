package net.thechance.mena.core_chat.domain.entity

data class WeatherDetails(
    val currentTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherType: WeatherType
)

enum class WeatherType {
    CLEAR_SKY,
    MAINLY_CLEAR,
    PARTLY_CLOUDY,
    OVERCAST,
    FOG,
    DEPOSITING_RIME_FOG,
    LIGHT_DRIZZLE,
    MODERATE_DRIZZLE,
    DENSE_DRIZZLE,
    LIGHT_FREEZING_DRIZZLE,
    DENSE_FREEZING_DRIZZLE,
    SLIGHT_RAIN,
    MODERATE_RAIN,
    HEAVY_RAIN,
    LIGHT_FREEZING_RAIN,
    HEAVY_FREEZING_RAIN,
    SLIGHT_SNOWFALL,
    MODERATE_SNOWFALL,
    HEAVY_SNOWFALL,
    SNOW_GRAINS,
    SLIGHT_RAIN_SHOWER,
    MODERATE_RAIN_SHOWER,
    VIOLENT_RAIN_SHOWER,
    SLIGHT_SNOW_SHOWER,
    HEAVY_SNOW_SHOWER,
    MODERATE_THUNDERSTORM,
    SLIGHT_HAIL_THUNDERSTORM,
    HEAVY_HAIL_THUNDERSTORM,
    UNKNOWN
}
