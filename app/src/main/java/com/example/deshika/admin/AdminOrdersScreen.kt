package com.example.deshika.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deshika.customer.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(navController: NavController, firestore: FirebaseFirestore) {
    val orders = remember { mutableStateListOf<Order>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            fetchAllOrders(firestore) { fetchedOrders, error ->
                if (error != null) {
                    errorMessage.value = error
                } else {
                    orders.clear()
                    orders.addAll(fetchedOrders)
                }
                isLoading.value = false
            }
        } catch (e: Exception) {
            errorMessage.value = "Failed to load orders: ${e.localizedMessage}"
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Orders") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading.value -> CircularProgressIndicator()
                errorMessage.value != null -> Text(
                    text = errorMessage.value!!,
                    fontSize = 18.sp,
                    color = Color.Red
                )
                orders.isEmpty() -> Text(text = "No orders found", fontSize = 18.sp, color = Color.Gray)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            AdminOrderCard(order, firestore)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, firestore: FirebaseFirestore) {
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel Order") },
            text = { Text("Are you sure you want to cancel this order?") },
            confirmButton = {
                TextButton(onClick = {
                    cancelOrder(firestore, order.orderId)
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Order") },
            text = { Text("Are you sure you want to confirm this order?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmOrder(firestore, order.orderId)
                    showConfirmDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: ${order.orderId}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Name: ${order.name}", fontSize = 14.sp)
            Text(text = "Phone: ${order.phone}", fontSize = 14.sp)
            Text(text = "Location: ${order.location}", fontSize = 14.sp)
            Text(text = "Size: ${order.size}", fontSize = 14.sp)
            Text(text = "Quantity: ${order.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Payment Method: ${order.paymentMethod}", fontSize = 14.sp)
            Text(text = "Total Amount: ${order.totalAmount} TK", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Delivery Charge: ${order.deliveryCharge} TK", fontSize = 14.sp)

            Text(
                text = "Status: ${order.status}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when (order.status) {
                    "Cancelled" -> Color.Red
                    "Confirmed" -> Color.Green
                    else -> Color.Blue
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Order", tint = Color.Red)
                }

                IconButton(onClick = { showConfirmDialog = true }) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Confirm Order", tint = Color.Green)
                }
            }
        }
    }
}

suspend fun fetchAllOrders(
    firestore: FirebaseFirestore,
    onOrdersFetched: (List<Order>, String?) -> Unit
) {
    try {
        val querySnapshot = firestore.collection("orders").get().await()

        if (!querySnapshot.isEmpty) {
            val orders = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)
            }
            onOrdersFetched(orders, null)
        } else {
            onOrdersFetched(emptyList(), null)
        }
    } catch (e: Exception) {
        onOrdersFetched(emptyList(), e.localizedMessage)
    }
}

fun cancelOrder(firestore: FirebaseFirestore, orderId: String) {
    firestore.collection("orders").document(orderId)
        .update("status", "Cancelled")
        .addOnSuccessListener {
            println("Order $orderId cancelled successfully")
        }
        .addOnFailureListener { e ->
            println("Failed to cancel order: ${e.localizedMessage}")
        }
}

fun confirmOrder(firestore: FirebaseFirestore, orderId: String) {
    firestore.collection("orders").document(orderId)
        .update("status", "Confirmed")
        .addOnSuccessListener {
            println("Order $orderId confirmed successfully")
        }
        .addOnFailureListener { e ->
            println("Failed to confirm order: ${e.localizedMessage}")
        }
}
