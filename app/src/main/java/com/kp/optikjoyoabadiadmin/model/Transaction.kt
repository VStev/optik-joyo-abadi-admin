package com.kp.optikjoyoabadiadmin.model

import com.google.firebase.Timestamp

data class Transaction(
    val transactionId: String = "",
    val consumerId: String = "",
    val headerTitle: String = "",
    val recipientName: String = "",
    val street: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: Int = 0,
    val phoneNumber: String = "",
    val shippingNumber: String? = "",
    val status: String = "",
    val subTotal: Int = 0,
    val shippingFee: Int = 0,
    val total: Int = 0,
    val dateTime: Timestamp = Timestamp.now()
)