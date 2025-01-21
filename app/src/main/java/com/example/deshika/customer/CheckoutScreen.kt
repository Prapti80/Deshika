package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(viewModel: CustomerViewModel, navToShipping: () -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalCost = cartItems.sumOf { it.price }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Shipping Address:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = navToShipping, modifier = Modifier.fillMaxWidth()) {
                Text("Add Shipping Address")
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(product.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(product.name, style = MaterialTheme.typography.headlineSmall)
                            Text("$${product.price}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Total Cost: $${totalCost}", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            Button(onClick = { /* Handle payment navigation */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Continue to Payment")
            }
        }
    }
}
