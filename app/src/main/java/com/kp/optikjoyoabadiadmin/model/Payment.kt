package com.kp.optikjoyoabadiadmin.model

data class Payment(
    val paymentId: String,
    val transactionId: String,
    val amount: Int,
    val proof: String,
    val receivedAt: com.google.firebase.Timestamp
)
