package net.thechance.mena.trends.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    /* example of writing Routes , delete this when write the first route in your user story  :

    <-- Normal Route -->

     @Serializable
     data object Trends : Route

    <-- when we need send parameter while navigation ,define the Route like this : -->

     @Serializable
     data class Trends (val id : Int) : Route

     */

    @Serializable
    data object Test: Route
}