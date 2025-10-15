package net.thechance.mena.faith.presentation.feature.main

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.util.extentions.getHijriDate
import net.thechance.mena.faith.presentation.util.extentions.getSunriseTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MainViewModel(
    private val quranRepository: QuranRepository,
    private val prayerTimeRepository: PrayerTimeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<MainScreenState, MainScreenEffect>(
    initialState = MainScreenState(),
), MainInteractionListener {

    init {
        initializeScreenData()
    }

    private fun initializeScreenData() {
        loadPrayerTimes()
        loadLastAyahForTilawah()
    }

    @OptIn(ExperimentalTime::class)
    private fun loadPrayerTimes() {
        tryToExecute(
            execute = {
                val defaultLocation = Location(latitude = 30.186173, longitude = 31.158446)
                prayerTimeRepository.getPrayerTimes(
                    date = Clock.System.now(),
                    location = defaultLocation
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = { prayerTimes -> onGetPrayerTimesSuccess(prayerTimes) },
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun
            onGetPrayerTimesSuccess(prayerTimes: List<PrayerTime>) {
        updateState { currentState ->
            currentState.copy(
                prayerTimes = prayerTimes,
                prayerTimesUiState = prayerTimes.toUi(Clock.System.now()),
                hijriDate = getHijriDate(prayerTimes),
                sunriseTime = getSunriseTime(prayerTimes)
            )
        }
    }

    private fun loadLastAyahForTilawah() {
        tryToExecute(
            execute = { quranRepository.getLastAyahForTilawah() },
            onSuccess = { ayah -> onGetLastAyahForTilawahSuccess(ayah) },
            dispatcher = dispatcher
        )
    }

    private fun onGetLastAyahForTilawahSuccess(ayah: LastAyahForTilawah) {
        tryToExecute(
            execute = { ayah.toTilawahUiState() },
            onSuccess = { tilawahState ->
                updateState { currentState ->
                    currentState.copy(tilawahUiState = tilawahState)
                }
            },
            dispatcher = dispatcher
        )
    }

    override fun onContinueTilawahClick(surahId: Int, surahName: String) =
        sendEffect(MainScreenEffect.NavigateToSurah(surahId, surahName))

    override fun onQuranClick() = sendEffect(MainScreenEffect.NavigateToQuran)

    override fun onQiblahClick() = sendEffect(MainScreenEffect.NavigateToQiblah)

    override fun onMosquesClick() = sendEffect(MainScreenEffect.NavigateToMosques)
}
