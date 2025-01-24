package com.example.deshika.customer

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val location: String = "",
    val size: String = "",
    val paymentMethod: String = "",
    val totalAmount: Double = 0.0,
    val deliveryCharge: Int = 0,
    val status: String = "Pending",  // New status field
    val timestamp: Long = 0
)
