package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCartScreen(viewModel: CustomerViewModel, navToCheckout: () -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalCost = cartItems.sumOf { it.price }
    var showRemoveDialog by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Cart") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Cart Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { product ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product Image
                        Image(
                            painter = rememberAsyncImagePainter(product.imageUrl),
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RectangleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        // Product Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("Size: XL", color = Color.Gray) // Replace with actual size if available
                            Text("$${product.price}", fontWeight = FontWeight.Bold)
                        }

                        // Quantity and Remove Button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* Handle decrease quantity */ }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrease Quantity")
                            }
                            Text("1") // Replace with actual quantity if available
                            IconButton(onClick = { /* Handle increase quantity */ }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { showRemoveDialog = product }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Item", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sub-Total", color = Color.Gray)
                        Text("$${totalCost}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", color = Color.Gray)
                        Text("$25.00") // Replace with dynamic delivery fee
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", color = Color.Gray)
                        Text("-$35.00") // Replace with dynamic discount
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Cost", fontWeight = FontWeight.Bold)
                        Text("$${totalCost + 25 - 35}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkout Button
            Button(
                onClick = navToCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
            ) {
                Text("Proceed to Checkout", color = Color.White)
            }
        }

        // Remove Confirmation Dialog
        if (showRemoveDialog != null) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = null },
                title = { Text("Remove from Cart?") },
                text = {
                    Text("Are you sure you want to remove ${showRemoveDialog?.name} from the cart?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.removeFromCart(showRemoveDialog!!)
                            showRemoveDialog = null
                        }
                    ) {
                        Text("Yes, Remove")
                    }
                },
                dismissButton = {
                    Button(onClick = { showRemoveDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
