package com.kp.optikjoyoabadiadmin.model

import com.google.firebase.Timestamp

data class Payment(
    val paymentId: String = "",
    val transactionId: String = "",
    val amount: Int = 0,
    val proof: String = "",
    val receivedAt: Timestamp = Timestamp.now()
)
