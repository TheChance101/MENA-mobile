package net.thechance.mena.faith.presentation.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class AzimuthProvider(context: Context) : SensorEventListener {
    private val _azimuth = MutableStateFlow(0f)
    actual val azimuthFlow: Flow<Float> = _azimuth

    private var sensorManager: SensorManager? = null
    private var accelValues = FloatArray(3)
    private var magnetValues = FloatArray(3)
    private var initialized = false

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        initialized = true
    }

    actual fun startListening() {
        if (!initialized)
            throw IllegalStateException("AzimuthProvider not initialized — call initialize() first.")

        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    actual fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> accelValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magnetValues = event.values.clone()
        }

        val rotationMatrix = FloatArray(9)
        val orientationValues = FloatArray(3)

        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magnetValues)) {
            SensorManager.getOrientation(rotationMatrix, orientationValues)
            val azimuthInDegrees = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
            val normalized = (azimuthInDegrees + 360) % 360
            _azimuth.value = normalized
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}