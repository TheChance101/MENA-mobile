package net.thechance.mena.faith.presentation.map

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

object MapConfigurator {

    fun configure(
        mapView: MapView,
        initialZoom: Double,
        centerLatitude: Double,
        centerLongitude: Double
    ) {
        configureTileSource(mapView)
        configureControls(mapView)
        configureZoom(mapView, initialZoom)
        configureBounds(mapView)
        configureCenter(mapView, centerLatitude, centerLongitude)
        disableRotation(mapView)
    }

    private fun configureTileSource(mapView: MapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
    }

    private fun configureControls(mapView: MapView) {
        mapView.setMultiTouchControls(true)
    }

    private fun configureZoom(mapView: MapView, initialZoom: Double) {
        mapView.minZoomLevel = MapConstants.MIN_ZOOM_LEVEL
        mapView.controller.setZoom(initialZoom)
    }

    private fun configureBounds(mapView: MapView) {
        mapView.isHorizontalMapRepetitionEnabled = false
        mapView.isVerticalMapRepetitionEnabled = false

        val boundingBox = BoundingBox(
            MapConstants.MAX_LATITUDE,
            MapConstants.MAX_LONGITUDE,
            MapConstants.MIN_LATITUDE,
            MapConstants.MIN_LONGITUDE
        )
        mapView.setScrollableAreaLimitDouble(boundingBox)

        mapView.setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude,
            MapView.getTileSystem().minLatitude,
            0
        )
    }

    private fun configureCenter(mapView: MapView, latitude: Double, longitude: Double) {
        mapView.controller.setCenter(GeoPoint(latitude, longitude))
    }

    private fun disableRotation(mapView: MapView) {
        val rotationOverlay = RotationGestureOverlay(mapView).apply {
            isEnabled = false
        }
        mapView.overlays.add(rotationOverlay)
    }
}