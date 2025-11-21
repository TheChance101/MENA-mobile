package net.thechance.mena.core_chat.presentation.screen.home

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.asr
import mena.core_chat_presentation.generated.resources.dhuhr
import mena.core_chat_presentation.generated.resources.fajr
import mena.core_chat_presentation.generated.resources.isha
import mena.core_chat_presentation.generated.resources.maghrib
import mena.core_chat_presentation.generated.resources.sunrise
import mena.core_chat_presentation.generated.resources.weather_clear_sky
import mena.core_chat_presentation.generated.resources.weather_dense_drizzle
import mena.core_chat_presentation.generated.resources.weather_dense_freezing_drizzle
import mena.core_chat_presentation.generated.resources.weather_depositing_rime_fog
import mena.core_chat_presentation.generated.resources.weather_fog
import mena.core_chat_presentation.generated.resources.weather_heavy_freezing_rain
import mena.core_chat_presentation.generated.resources.weather_heavy_hail_thunderstorm
import mena.core_chat_presentation.generated.resources.weather_heavy_rain
import mena.core_chat_presentation.generated.resources.weather_heavy_snow_shower
import mena.core_chat_presentation.generated.resources.weather_heavy_snowfall
import mena.core_chat_presentation.generated.resources.weather_light_drizzle
import mena.core_chat_presentation.generated.resources.weather_light_freezing_drizzle
import mena.core_chat_presentation.generated.resources.weather_light_freezing_rain
import mena.core_chat_presentation.generated.resources.weather_mainly_clear
import mena.core_chat_presentation.generated.resources.weather_moderate_drizzle
import mena.core_chat_presentation.generated.resources.weather_moderate_rain
import mena.core_chat_presentation.generated.resources.weather_moderate_rain_shower
import mena.core_chat_presentation.generated.resources.weather_moderate_snowfall
import mena.core_chat_presentation.generated.resources.weather_moderate_thunderstorm
import mena.core_chat_presentation.generated.resources.weather_overcast
import mena.core_chat_presentation.generated.resources.weather_partly_cloudy
import mena.core_chat_presentation.generated.resources.weather_slight_hail_thunderstorm
import mena.core_chat_presentation.generated.resources.weather_slight_rain
import mena.core_chat_presentation.generated.resources.weather_slight_rain_shower
import mena.core_chat_presentation.generated.resources.weather_slight_snow_shower
import mena.core_chat_presentation.generated.resources.weather_slight_snowfall
import mena.core_chat_presentation.generated.resources.weather_snow_grains
import mena.core_chat_presentation.generated.resources.weather_violent_rain_shower
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.entity.WeatherType
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Read
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Received
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Sent
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.UnRead
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.PrayerUiState
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import org.jetbrains.compose.resources.StringResource
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): ChatUiState {

    val statusMessages = getStatusMessages(lastMessage, unReadMessagesCount)
    val lastMessage = lastMessage?.let {
        ChatUiState.MessageUiState(
            text = it.content,
            isMine = it.isMine,
            time = it.sendAt,
        )
    }
    return ChatUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        status = statusMessages
    )
}

private fun getStatusMessages(lastMessage: ChatSummary.Message?, unReadMessagesCount: Int): Status {
    if (lastMessage == null) return Received
    return when {
        lastMessage.isMine -> {
            if (unReadMessagesCount == 0) {
                Read
            } else {
                Sent
            }
        }

        else -> {
            if (unReadMessagesCount > 0) {
                UnRead(unReadMessagesCount)
            } else {
                Received
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
fun PrayerTime.toUi(): PrayerUiState = PrayerUiState(
    displayName = getPrayerDisplayNameResource(prayerName = this.name),
    time = this.time.toLocalDateTime(TimeZone.currentSystemDefault()),
)

fun getPrayerDisplayNameResource(prayerName: PrayerName): StringResource = when (prayerName) {
    PrayerName.FAJR -> Res.string.fajr
    PrayerName.DHUHR -> Res.string.dhuhr
    PrayerName.ASR -> Res.string.asr
    PrayerName.MAGHRIB -> Res.string.maghrib
    PrayerName.ISHA -> Res.string.isha
    PrayerName.SUNRISE -> Res.string.sunrise
}

fun WeatherDetails.toUi(): HomeScreenState.WeatherUiState = HomeScreenState.WeatherUiState(
    currentTemperature = currentTemperature.toString(),
    weatherCondition = weatherType.toUi(),
    maxTemperature = maxTemperature.toString(),
    minTemperature = minTemperature.toString()
)

private fun WeatherType.toUi(): StringResource? = when (this) {
    WeatherType.CLEAR_SKY -> Res.string.weather_clear_sky
    WeatherType.MAINLY_CLEAR -> Res.string.weather_mainly_clear
    WeatherType.PARTLY_CLOUDY -> Res.string.weather_partly_cloudy
    WeatherType.OVERCAST -> Res.string.weather_overcast
    WeatherType.FOG -> Res.string.weather_fog
    WeatherType.DEPOSITING_RIME_FOG -> Res.string.weather_depositing_rime_fog
    WeatherType.LIGHT_DRIZZLE -> Res.string.weather_light_drizzle
    WeatherType.MODERATE_DRIZZLE -> Res.string.weather_moderate_drizzle
    WeatherType.DENSE_DRIZZLE -> Res.string.weather_dense_drizzle
    WeatherType.LIGHT_FREEZING_DRIZZLE -> Res.string.weather_light_freezing_drizzle
    WeatherType.DENSE_FREEZING_DRIZZLE -> Res.string.weather_dense_freezing_drizzle
    WeatherType.SLIGHT_RAIN -> Res.string.weather_slight_rain
    WeatherType.MODERATE_RAIN -> Res.string.weather_moderate_rain
    WeatherType.HEAVY_RAIN -> Res.string.weather_heavy_rain
    WeatherType.LIGHT_FREEZING_RAIN -> Res.string.weather_light_freezing_rain
    WeatherType.HEAVY_FREEZING_RAIN -> Res.string.weather_heavy_freezing_rain
    WeatherType.SLIGHT_SNOWFALL -> Res.string.weather_slight_snowfall
    WeatherType.MODERATE_SNOWFALL -> Res.string.weather_moderate_snowfall
    WeatherType.HEAVY_SNOWFALL -> Res.string.weather_heavy_snowfall
    WeatherType.SNOW_GRAINS -> Res.string.weather_snow_grains
    WeatherType.SLIGHT_RAIN_SHOWER -> Res.string.weather_slight_rain_shower
    WeatherType.MODERATE_RAIN_SHOWER -> Res.string.weather_moderate_rain_shower
    WeatherType.VIOLENT_RAIN_SHOWER -> Res.string.weather_violent_rain_shower
    WeatherType.SLIGHT_SNOW_SHOWER -> Res.string.weather_slight_snow_shower
    WeatherType.HEAVY_SNOW_SHOWER -> Res.string.weather_heavy_snow_shower
    WeatherType.MODERATE_THUNDERSTORM -> Res.string.weather_moderate_thunderstorm
    WeatherType.SLIGHT_HAIL_THUNDERSTORM -> Res.string.weather_slight_hail_thunderstorm
    WeatherType.HEAVY_HAIL_THUNDERSTORM -> Res.string.weather_heavy_hail_thunderstorm
    WeatherType.UNKNOWN -> null
}
