package com.kp.optikjoyoabadiadmin.model

data class TransactionDetail(
    val transactionId: String = "",
    val productId: String = "",
    val productName: String = "",
    val price: Int = 0,
    val notes: String = "",
    val quantity: Int = 0,
    val image_url: String = ""
)
