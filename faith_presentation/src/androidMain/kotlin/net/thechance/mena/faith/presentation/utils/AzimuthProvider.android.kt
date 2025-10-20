package net.thechance.mena.faith.presentation.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class AzimuthProviderImpl(context: Context) : AzimuthProvider {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    actual override fun startListening(): Flow<Float> = callbackFlow {
        var accelerateValues = FloatArray(3)
        var magnetValues = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                when (event?.sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> accelerateValues = event.values.clone()
                    Sensor.TYPE_MAGNETIC_FIELD -> magnetValues = event.values.clone()
                    else -> return
                }
                val rotationMatrix = FloatArray(9)
                val orientationValues = FloatArray(3)
                val isRotationMatrixValid = SensorManager.getRotationMatrix(
                    rotationMatrix, null, accelerateValues, magnetValues
                )
                if (isRotationMatrixValid) {
                    SensorManager.getOrientation(rotationMatrix, orientationValues)
                    val azimuthInDegrees = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    val normalizedAzimuth = (azimuthInDegrees + 360) % 360
                    trySend(normalizedAzimuth)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}