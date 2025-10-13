package net.thechance.mena.faith.presentation.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

actual class AzimuthProvider {

    val locationManager = LocationManager()

    actual val azimuthFlow: Flow<Float> = flowOf(0f)

    actual fun startListening() {}

    actual fun stopListening() {}
}

class LocationManager : NSObject(), CLLocationManagerDelegateProtocol