package com.kp.optikjoyoabadiadmin.model

data class TransactionDetail(
    val transactionId: String,
    val productId: String,
    val productName: String,
    val details: String,
    val category: String,
    val shape: String,
    val price: Int,
    val notes: String,
    val quantity: Int,
    val image_url: String
)
