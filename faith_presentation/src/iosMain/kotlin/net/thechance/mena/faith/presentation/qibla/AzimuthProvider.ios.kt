package net.thechance.mena.faith.presentation.qibla

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

actual class AzimuthProvider : NSObject(), CLLocationManagerDelegateProtocol {


    actual val azimuthFlow: Flow<Float> = flowOf(0f)


    actual fun startListening() {
    }

    actual fun stopListening() {
    }
}
