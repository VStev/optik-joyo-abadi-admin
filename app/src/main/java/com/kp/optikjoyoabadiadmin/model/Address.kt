package com.kp.optikjoyoabadiadmin.model

data class Address(
    val addressId: String,
    val consumerId: String,
    val recipientName: String,
    val street: String,
    val region: String,
    val city: String,
    val postalCode: Int,
    val phoneNumber: String
)
