package com.muei.travelmate.ui.route

class LocationProvider {
    companion object{
        // TODO : Este objeto debe tener como minimo dos elemntos y posiblemente no superar cierta
        // cantidad, replantearse como hacerlo
        val routeList = mutableListOf<Location>(
            Location("","", LocationType.CITY, "", ""),
            Location("","", LocationType.CITY, "", "")
        )
    }


}