package net.thechance.mena.faith.presentation.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import net.thechance.mena.faith.presentation.R

object ClusterIconFactory {

    fun createClusterIcon(context: Context, count: Int): Drawable {
        val vectorDrawable = ContextCompat.getDrawable(context, R.drawable.marker_cluster)
            ?: Color.BLACK.toDrawable()

        val bitmap = createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val canvas = Canvas(bitmap)

        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        drawClusterCount(canvas, count)

        return bitmap.toDrawable(context.resources)
    }

    private fun drawClusterCount(canvas: Canvas, count: Int) {
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = MapConstants.CLUSTER_TEXT_SIZE
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val x = canvas.width / 2f
        val y = canvas.height / 2f + MapConstants.CLUSTER_TEXT_OFFSET_Y

        canvas.drawText(count.toString(), x, y, paint)
    }
}