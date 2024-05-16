package com.muei.travelmate.ui.route
data class Location (
    val name:String,
    val image:String,
    val type:LocationType,
    var _searchTerm:String,
){
    var searchTerm: String
        get(){
            // si no se define searchterm devuelve name
            if (_searchTerm=="")
                return name
            return _searchTerm
        }
        set(value) {
            _searchTerm = value
        }
    override fun toString(): String {
        return name
    }

}