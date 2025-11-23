package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.map.MapConstants
import net.thechance.mena.faith.presentation.map.MapMarkerManager
import net.thechance.mena.faith.presentation.map.createClusterIcon
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationDegrees
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.darwin.NSObject
import kotlin.math.abs
import kotlin.math.ln


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double,
    markers: List<MosqueUiState>,
    canMove: Boolean,
    onMarkerClick: (MosqueUiState) -> Unit,
    onCameraMove: (Double, Double) -> Unit,
    onMapIdle: (Double, Double) -> Unit
) {
    val markerImage = remember { UIImage.imageNamed("ic_mosque_marker") }
    var currentZoom by remember { mutableDoubleStateOf(zoomLevel) }
    val mapMarkerManager = MapMarkerManager()
    val mapKitController = remember {
        object {
            val delegate = object : NSObject(), MKMapViewDelegateProtocol {
                @ObjCSignatureOverride
                override fun mapView(mapView: MKMapView, regionWillChangeAnimated: Boolean) {
                    val currentRegion = mapView.region
                    val centerLat = currentRegion.useContents { this.center.latitude }
                    val centerLong = currentRegion.useContents { this.center.longitude }
                    val spanValue = currentRegion.useContents { this.span.latitudeDelta }

                    val newZoom = if (spanValue > 0) calculateZoomLevel(spanValue) else zoomLevel

                    onCameraMove(centerLat, centerLong)

                    val zoomDifference = abs(newZoom - currentZoom)
                    if (zoomDifference >= 0.3) {
                        currentZoom = newZoom
                        mapMarkerManager.updateMarkers(
                            mapView = mapView,
                            mosques = markers,
                            zoomLevel = newZoom
                        )
                    }
                }

                @ObjCSignatureOverride
                override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
                    val currentRegion = mapView.region
                    val centerLat = currentRegion.useContents { this.center.latitude }
                    val centerLong = currentRegion.useContents { this.center.longitude }
                    onMapIdle(centerLat, centerLong)
                }

                override fun mapView(
                    mapView: MKMapView,
                    viewForAnnotation: MKAnnotationProtocol
                ): MKAnnotationView? {
                    val pointAnnotation = viewForAnnotation as? MKPointAnnotation ?: return null
                    val title = pointAnnotation.title
                    val isCluster = title != null && title.toIntOrNull() != null

                    return if (isCluster) {
                        createClusterAnnotationView(
                            mapView = mapView,
                            viewForAnnotation = viewForAnnotation,
                            title = title
                        )
                    } else {
                        createMarkerAnnotationView(
                            mapView = mapView,
                            viewForAnnotation = viewForAnnotation,
                            markerImage = markerImage
                        )
                    }
                }

                override fun mapView(
                    mapView: MKMapView,
                    didSelectAnnotationView: MKAnnotationView
                ) {
                    val coordinate = didSelectAnnotationView.annotation?.coordinate
                    val latitude = coordinate?.useContents { this.latitude }
                    val longitude = coordinate?.useContents { this.longitude }
                    markers.find {
                        it.coordinate.latitude == latitude && it.coordinate.longitude == longitude
                    }
                        ?.let { mosque ->
                            onMarkerClick(mosque)
                        }
                }
            }
        }
    }

    UIKitView(
        modifier = modifier.fillMaxSize(),
        factory = {
            val mapView = MKMapView()
            mapView.showsUserLocation = false
            mapView.setRotateEnabled(false)
            mapView.setPitchEnabled(false)
            mapView.setZoomEnabled(true)
            mapView.setScrollEnabled(true)

            val coordinate = CLLocationCoordinate2DMake(centerLatitude, centerLongitude)
            val spanDelta = 0.1 * (20.0 - zoomLevel)
            val span = MKCoordinateSpanMake(latitudeDelta = spanDelta, longitudeDelta = spanDelta)
            val region = MKCoordinateRegionMake(coordinate, span)
            mapView.setRegion(region, animated = false)

            mapView.delegate = mapKitController.delegate
            mapView
        },
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun createClusterAnnotationView(
    mapView: MKMapView,
    viewForAnnotation: MKPointAnnotation,
    title: String
): MKAnnotationView {
    val identifier = "ClusterPin"
    var annotationView =
        mapView.dequeueReusableAnnotationViewWithIdentifier(identifier)

    if (annotationView == null) {
        annotationView = MKAnnotationView(
            annotation = viewForAnnotation,
            reuseIdentifier = identifier
        )
    } else {
        annotationView.annotation = viewForAnnotation
    }

    annotationView.canShowCallout = false
    val count = title.toIntOrNull() ?: 0
    annotationView.image = createClusterIcon(count)
    annotationView.centerOffset = CGPointMake(x = 0.0, y = 0.0)

    return annotationView
}

@OptIn(ExperimentalForeignApi::class)
private fun createMarkerAnnotationView(
    mapView: MKMapView,
    viewForAnnotation: MKPointAnnotation,
    markerImage: UIImage?
): MKAnnotationView? {
    val identifier = "MosquePin"
    var annotationView =
        mapView.dequeueReusableAnnotationViewWithIdentifier(identifier)

    if (annotationView == null) {
        annotationView = MKAnnotationView(
            annotation = viewForAnnotation,
            reuseIdentifier = identifier
        )
    } else {
        annotationView.annotation = viewForAnnotation
    }

    annotationView.canShowCallout = false

    markerImage?.let {
        configureAnnotationImage(
            markerImage = markerImage,
            annotationView = annotationView
        )
    } ?: return null

    return annotationView
}

@OptIn(ExperimentalForeignApi::class)
private fun configureAnnotationImage(
    markerImage: UIImage,
    annotationView: MKAnnotationView
) {
    val targetSize = CGSizeMake(width = 45.0, height = 58.0)
    val renderer = UIGraphicsImageRenderer(size = targetSize)
    val width = targetSize.useContents { this.width }
    val height = targetSize.useContents { this.height }
    val resizedImage = renderer.imageWithActions { context ->
        markerImage.drawInRect(CGRectMake(x = 0.0, y = 0.0, width, height))
    }
    annotationView.image = resizedImage
    annotationView.centerOffset = CGPointMake(x = 0.0, y = 0.0)
}

private fun calculateZoomLevel(spanValue: CLLocationDegrees): Double {
    return (ln(360.0 / spanValue) / ln(2.0)).coerceIn(
        MapConstants.MIN_ZOOM_LEVEL,
        MapConstants.MAX_ZOOM_LEVEL
    )
}
