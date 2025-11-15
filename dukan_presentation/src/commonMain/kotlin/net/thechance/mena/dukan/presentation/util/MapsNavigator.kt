package net.thechance.mena.dukan.presentation.util

expect object MapsNavigator{
    fun getDirections(
        startLat : Double,
        startLng : Double,
        endLat : Double,
        endLng : Double,
        context: Any? = null
    )
}