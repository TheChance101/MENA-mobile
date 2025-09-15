package net.thechance.mena.trends.presentation.screen

import androidx.lifecycle.ViewModel
import net.thechance.mena.trends.domain.repository.ReelRepository
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/*
* This is an example of how you can use koin annotation with view model
* - you can use @KoinViewModel just like that
* - @Provided here tell the koin to go and get the instance from modules which you will provide
*/

@KoinViewModel
class TestViewModel(
    @Provided private val fakeRepo: ReelRepository
): ViewModel() {
}