package net.thechance.mena.dukan.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.currentLocationOrNull
import dev.jordond.compass.geolocation.mobile
import dev.jordond.compass.permissions.MobileLocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

class LocationRepositoryImpl : LocationRepository {
    override suspend fun getCurrentLocation(): Dukan.Coordinates? {
        val geolocator = Geolocator.mobile()
        val permissionController = MobileLocationPermissionController()

        if (!permissionController.hasPermission()) {
            val newState = permissionController.requirePermissionFor(Priority.Balanced)
            if (newState != PermissionState.Granted) {
                return null
            }
        }

        val currentLocation: Location? = geolocator.currentLocationOrNull()

        return currentLocation?.let {
            Dukan.Coordinates(
                latitude = it.coordinates.latitude,
                longitude = it.coordinates.longitude
            )
        }
    }

    override suspend fun getCurrentLocationName(
        coordinates: Dukan.Coordinates,
    ): String {
        val geocoder = MobileGeocoder().placeOrNull(
            Coordinates(
                coordinates.latitude,
                coordinates.longitude
            )
        )
        println("This is just test for the location goeCoder $geocoder")
        return geocoder?.let {
            "${it.subAdministrativeArea}, ${it.administrativeArea}, ${it.country}"
        } ?: ""
    }
}