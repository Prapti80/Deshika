package com.example.deshika.customer

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val location: String = "",
    val size: String = "",
    val quantity: Int = 1,  // New quantity field with a default value of 1
    val paymentMethod: String = "",
    val transactionId: String = "",
    val totalAmount: Double = 0.0,
    val deliveryCharge: Int = 0,
    val status: String = "Pending",  // New status field
    val timestamp: Long = 0
)
