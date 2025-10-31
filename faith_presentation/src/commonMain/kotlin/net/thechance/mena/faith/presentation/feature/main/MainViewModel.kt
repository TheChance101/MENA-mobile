package net.thechance.mena.faith.presentation.feature.main

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getHijriReadableDate
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.getSunriseTime
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MainViewModel(
    private val quranRepository: QuranRepository,
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<MainUiState, MainScreenEffect>(
    initialState = MainUiState(),
), MainInteractionListener {

    init {
        initializeScreenData()
    }

    private fun initializeScreenData() {
        loadPrayerTimes()
        loadLastAyahForTilawah()
    }

    private fun loadPrayerTimes() {
        tryToExecute(
            execute = {
                updateAddress(locationService.getActiveAddress())
                prayerTimeRepository.getPrayerTimes(
                    date = Clock.System.now(),
                    address = locationService.getActiveAddress()
                )
            },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = ::onGetPrayerTimesSuccess,
            onFinally = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private fun updateAddress(address: Address?) {
        if (address?.addressLine.isNullOrEmpty()) {
            sendEffect(MainScreenEffect.NavigateToEnableLocation)
            return
        }

        address?.let { updateState { state -> state.copy(address = it.addressLine) } }
    }

    private fun isValidAddress(address: String): Boolean = address.isNotEmpty()

    private fun onGetPrayerTimesSuccess(prayerTimes: List<PrayerTime>) {
        updateState { currentState ->
            currentState.copy(
                prayerTimes = prayerTimes,
                prayerTimesUiState = prayerTimes.toUi(Clock.System.now()),
                hijriDate = getHijriReadableDate(prayerTimes),
                sunriseTime = getSunriseTime(prayerTimes)
            )
        }
    }

    private fun loadLastAyahForTilawah() {
        tryToExecute(
            execute = { quranRepository.getLastAyahForTilawah() },
            onSuccess = ::onGetLastAyahForTilawahSuccess,
            dispatcher = dispatcher
        )
    }

    private suspend fun onGetLastAyahForTilawahSuccess(ayah: LastAyahForTilawah) {
        val tilawahState = ayah.toTilawahUiState()
        updateState { it.copy(tilawahUiState = tilawahState) }
    }

    override fun onContinueTilawahClick(surahId: Int, surahName: String, ayahNumber: Int) {
        sendEffect(
            MainScreenEffect.NavigateToSurah(
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber
            )
        )
    }

    override fun onQuranClick() = sendEffect(MainScreenEffect.NavigateToQuran)

    override fun onQiblahClick() = sendEffect(MainScreenEffect.NavigateToQiblah)

    override fun onMosquesClick() = sendEffect(MainScreenEffect.NavigateToMosques)

    override fun onChangeLocation() {
        if (isValidAddress(uiState.value.address)) {
            sendEffect(MainScreenEffect.NavigateToEnableLocation)
            return
        }
        sendEffect(MainScreenEffect.NavigateToMyLocation)
    }

    fun refreshTilawah() {
        loadLastAyahForTilawah()
    }
}
