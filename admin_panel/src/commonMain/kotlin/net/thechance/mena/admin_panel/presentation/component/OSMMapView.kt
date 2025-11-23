package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.thechance.mena.admin_panel.resources.Res
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.Waypoint
import org.jxmapviewer.viewer.WaypointPainter
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.swing.SwingUtilities

private const val BASE_URL = "https://a.basemaps.cartocdn.com/rastertiles/voyager_labels_under"
private const val FACTORY_NAME = "cartocdn"
private const val IMAGE_PATH = "drawable/map_marker.svg"

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OSMMapView(
    latitude: Double,
    longitude: Double,
    markerWidth: Int,
    markerHeight: Int,
    initialZoom: Int = 7,
    modifier: Modifier = Modifier
) {
    var markerIcon by remember { mutableStateOf<BufferedImage?>(null) }

    LaunchedEffect(Unit) {
        markerIcon = withContext(Dispatchers.IO) {
            val bytes = Res.readBytes(IMAGE_PATH)
            svgToBufferedImage(bytes, markerWidth, markerHeight)
        }
    }

    val markerPosition = remember(latitude, longitude) {
        GeoPosition(latitude, longitude)
    }

    if (markerIcon != null) {
        SwingPanel(
            factory = {
                createMapViewer(markerIcon!!, markerPosition, initialZoom)
            },
            modifier = modifier,
            update = { mapViewer ->
                updateMapViewerPosition(mapViewer, markerPosition, initialZoom)
            }
        )
    }
}

private fun createMapViewer(
    markerIcon: BufferedImage,
    markerPosition: GeoPosition,
    initialZoom: Int
): JXMapViewer {
    val mapViewer = JXMapViewer()

    mapViewer.tileFactory = DefaultTileFactory(
        OSMTileFactoryInfo(FACTORY_NAME, BASE_URL)
    )

    mapViewer.addressLocation = markerPosition
    mapViewer.zoom = initialZoom

    mapViewer.addPanAndZoomListeners()

    mapViewer.overlayPainter = CustomWaypointPainter(markerIcon).apply {
        setMarkerPosition(markerPosition)
    }

    SwingUtilities.invokeLater {
        mapViewer.addressLocation = markerPosition
        mapViewer.repaint()
    }

    return mapViewer
}

private fun JXMapViewer.addPanAndZoomListeners() {
    val panAdapter = PanMouseInputListener(this)
    addMouseListener(panAdapter)
    addMouseMotionListener(panAdapter)

    val zoomAdapter = ZoomMouseWheelListenerCursor(this)
    addMouseWheelListener(zoomAdapter)
}

private fun updateMapViewerPosition(
    mapViewer: JXMapViewer,
    markerPosition: GeoPosition,
    initialZoom: Int
) {
    (mapViewer.overlayPainter as? CustomWaypointPainter)?.setMarkerPosition(markerPosition)

    SwingUtilities.invokeLater {
        mapViewer.addressLocation = markerPosition
        mapViewer.zoom = initialZoom
        mapViewer.repaint()
    }
}

private fun svgToBufferedImage(
    svgBytes: ByteArray,
    markerWidth: Int,
    markerHeight: Int
): BufferedImage {
    val image = BufferedImage(markerWidth, markerHeight, BufferedImage.TYPE_INT_ARGB)

    try {
        val transcoder = object : ImageTranscoder() {
            override fun createImage(w: Int, h: Int): BufferedImage {
                return BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
            }

            override fun writeImage(img: BufferedImage, output: TranscoderOutput?) {
                val g = image.createGraphics()
                g.drawImage(img, 0, 0, null)
                g.dispose()
            }
        }

        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, markerWidth.toFloat())
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, markerHeight.toFloat())

        val input = TranscoderInput(ByteArrayInputStream(svgBytes))
        transcoder.transcode(input, null)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return image
}

class CustomWaypointPainter(private val markerIcon: BufferedImage) : WaypointPainter<Waypoint>() {
    private var markerPosition: GeoPosition? = null

    fun setMarkerPosition(position: GeoPosition) {
        markerPosition = position
        waypoints = setOf(DefaultWaypoint(position))
    }

    override fun doPaint(g: Graphics2D, map: JXMapViewer, width: Int, height: Int) {
        markerPosition?.let { position ->
            val point = map.tileFactory.geoToPixel(position, map.zoom)
            val x = (point.x - map.viewportBounds.x).toInt()
            val y = (point.y - map.viewportBounds.y).toInt()

            g.drawImage(
                markerIcon,
                x - markerIcon.width / 2,
                y - markerIcon.height,
                null
            )
        }
    }
}