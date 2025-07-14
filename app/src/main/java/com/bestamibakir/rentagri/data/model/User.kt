package com.bestamibakir.rentagri.data.model

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val countryCode: String = "+90",
    val country: String = "TR",
    val city: String = "",
    val province: String = "",
    val district: String = ""
) 