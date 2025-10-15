package net.thechance.mena.faith.presentation.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

actual class AzimuthProvider {
    private val locationManager = LocationManager()
    actual fun startListening(): Flow<Float> = flowOf(0f)
}

internal class LocationManager : NSObject(), CLLocationManagerDelegateProtocol