package net.thechance.mena.dukan.data.repository

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository
import java.util.Locale
import kotlin.coroutines.resumeWithException

actual class LocationRepositoryImpl(private val context: Context) : LocationRepository {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    actual override suspend fun getCurrentLocation(): Dukan.Location {
        suspendCancellableCoroutine { scope ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    val government = addresses?.firstOrNull()?.adminArea ?: "Unknown State"

                    scope.resume(
                        Dukan.Location(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = government,
                        )
                    ) { cause, _, _ -> scope.cancel(cause) }
                } else {
                    scope.resumeWithException(Exception("Location is null"))
                }
            }.addOnFailureListener {
                scope.resumeWithException(it)
            }
        }
    }
}