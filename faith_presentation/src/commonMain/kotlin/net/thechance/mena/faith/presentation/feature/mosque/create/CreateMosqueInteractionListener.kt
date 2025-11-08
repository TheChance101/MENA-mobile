package net.thechance.mena.faith.presentation.feature.mosque.create

import androidx.compose.ui.unit.DpOffset
import com.attafitamim.krop.core.images.ImageSrc
import io.github.dellisd.spatialk.geojson.Position

internal interface CreateMosqueInteractionListener {
    fun onBackClick()
    fun onClickUploadImage(image: ImageSrc)
    fun onAddClick()
    fun onNameChange(name: String)
    fun onAddressChange(address: String)
    fun onMapClick(position: Position, offset: DpOffset)
    fun onEditMarkerClick()
}