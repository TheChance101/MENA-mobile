package net.thechance.mena.faith.presentation.util

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

actual class AzimuthProvider {
    private val locationManager = CLLocationManager()

    actual fun startListening(): Flow<Float> = callbackFlow {
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(
                manager: CLLocationManager,
                didUpdateHeading: CLHeading
            ) {
                // Azimuth in degrees (0-360)
                val azimuth = didUpdateHeading.trueHeading.toFloat()
                println("Azimuth: $azimuth")
                trySend(azimuth)
            }

            override fun locationManager(
                manager: CLLocationManager,
                didFailWithError: NSError
            ) {
                close(Exception(didFailWithError.localizedDescription))
            }
        }

        locationManager.delegate = delegate
        locationManager.startUpdatingHeading()

        awaitClose {
            locationManager.stopUpdatingHeading()
            locationManager.delegate = null
        }
    }
}