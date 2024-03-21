package com.muei.travelmate.ui.auth

import com.auth0.android.jwt.JWT

data class User(val idToken:String?=null) {
    var id = ""
    var name = ""
    var email = ""
    var emailverified = ""
    var picture = ""
    var updatedAt = ""

    init {
        try {
            // decode the id token
            val jwt:JWT = JWT(idToken?: "")
            id = jwt.subject ?: ""
            name = jwt.getClaim("name").asString() ?: ""
            email  = jwt.getClaim("email").asString() ?: ""
            emailverified = jwt.getClaim("email_verified").asString() ?: ""
            picture  = jwt.getClaim("picture").asString() ?: ""
            updatedAt  = jwt.getClaim("updated_at").asString() ?: ""
        }catch (e: com.auth0.android.jwt.DecodeException){

        }
    }
}