package com.kp.optikjoyoabadiadmin.model

data class Transaction(
    val transactionId: String,
    val consumerId: String,
    val recipientName: String,
    val street: String,
    val city: String,
    val region: String,
    val postalCode: Int,
    val phoneNumber: Int,
    val shippingNumber: String?,
    val status: String,
    val subtotal: Int,
    val shippingFee: Int,
    val total: Int,
    val dateTime: com.google.firebase.Timestamp
)