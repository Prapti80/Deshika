package com.example.deshika.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(viewModel: CustomerViewModel, navToReview: (Order) -> Unit, navToReorder: (Order) -> Unit) {
    val orders by viewModel.orders.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Orders") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Orders List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val filteredOrders = when (selectedTabIndex) {
                    0 -> orders.filter { it.status == "Active" }
                    1 -> orders.filter { it.status == "Completed" }
                    2 -> orders.filter { it.status == "Cancelled" }
                    else -> emptyList()
                }

                items(filteredOrders) { order ->
                    OrderCard(order, selectedTabIndex, navToReview, navToReorder)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, tabIndex: Int, navToReview: (Order) -> Unit, navToReorder: (Order) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
               .shadow(4.dp)

    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Order Info
            Column(modifier = Modifier.weight(1f)) {
                Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                order.products.forEach { product ->
                    Text("${product.name} | Size: ${product.size} | Qty: ${product.price}pcs", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total: $${order.total}", style = MaterialTheme.typography.bodyLarge)
            }

            // Action Button
            when (tabIndex) {
                0 -> Button(onClick = { /* Track Order */ }) {
                    Text("Track Order")
                }
                1 -> Button(onClick = { navToReview(order) }) {
                    Text("Leave Review")
                }
                2 -> Button(onClick = { navToReorder(order) }) {
                    Text("Re-Order")
                }
            }
        }
    }
}
