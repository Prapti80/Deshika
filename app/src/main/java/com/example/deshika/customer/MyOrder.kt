package com.example.deshika.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController, firestore: FirebaseFirestore) {
    val user = FirebaseAuth.getInstance().currentUser
    val orders = remember { mutableStateListOf<Order>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (user != null) {
            try {
                fetchUserOrders(firestore, user.uid) { fetchedOrders, error ->
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
        } else {
            errorMessage.value = "User not logged in"
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
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
                isLoading.value -> {
                    CircularProgressIndicator()
                }

                errorMessage.value != null -> {
                    Text(
                        text = errorMessage.value!!,
                        fontSize = 18.sp,
                        color = Color.Red
                    )
                }

                orders.isEmpty() -> {
                    Text(text = "No orders found", fontSize = 18.sp, color = Color.Gray)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderCard(order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status: ${order.status}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when (order.status) {
                    "Pending" -> Color.Yellow
                    "Confirmed" -> Color.Green
                    "Cancelled" -> Color.Red
                    else -> Color.Gray
                }
            )
        }
    }
}

suspend fun fetchUserOrders(
    firestore: FirebaseFirestore,
    userId: String,
    onOrdersFetched: (List<Order>, String?) -> Unit
) {
    try {
        val querySnapshot = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val orders = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)
            }
            println("Fetched ${orders.size} orders for user ID: $userId")
            onOrdersFetched(orders, null)
        } else {
            println("No orders found for user ID: $userId")
            onOrdersFetched(emptyList(), null)
        }
    } catch (e: Exception) {
        println("Error fetching orders: ${e.localizedMessage}")
        onOrdersFetched(emptyList(), e.localizedMessage)
    }
}
