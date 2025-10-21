package net.thechance.mena.faith.presentation.utils

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

actual class AzimuthProviderImpl : AzimuthProvider {
    private val locationManager = CLLocationManager()

    actual override fun startListening(): Flow<Float> = callbackFlow {
        val locationManagerDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(
                manager: CLLocationManager,
                didUpdateHeading: CLHeading
            ) {
                val azimuth = didUpdateHeading.trueHeading.toFloat()
                trySend(azimuth)
            }

            override fun locationManager(
                manager: CLLocationManager,
                didFailWithError: NSError
            ) {
                close(Exception(didFailWithError.localizedDescription))
            }
        }

        locationManager.delegate = locationManagerDelegate
        locationManager.startUpdatingHeading()

        awaitClose {
            locationManager.stopUpdatingHeading()
            locationManager.delegate = null
        }
    }
}