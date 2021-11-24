package com.kp.optikjoyoabadiadmin.model

data class Product (
    val productId: String = "",
    val productName: String = "",
    val category: String = "",
    val shape: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val details: String = "",
    val image_url: String = ""
    )