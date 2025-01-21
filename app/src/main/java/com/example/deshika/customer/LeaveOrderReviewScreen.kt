package com.example.deshika.customer

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.deshika.customer.CustomerViewModel
import com.example.deshika.customer.Order
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveOrderReviewScreen(viewModel: CustomerViewModel, order: Order, navBack: () -> Unit) {
    val reviewText = remember { mutableStateOf("") }
    val rating = remember { mutableStateOf(0) }
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Leave Review") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Product Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(order.products.firstOrNull()?.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(order.products.firstOrNull()?.name ?: "Product Name", style = MaterialTheme.typography.headlineSmall)
                    Text("Size: ${order.products.firstOrNull()?.size}", style = MaterialTheme.typography.bodyMedium)
                    Text("$${order.products.firstOrNull()?.price}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Review Title
            Text("How is your order?", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Rating Stars
            Row(modifier = Modifier.padding(vertical = 16.dp)) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = if (star <= rating.value) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { rating.value = star }
                    )
                }
            }

            // Add Detailed Review
            OutlinedTextField(
                value = reviewText.value,
                onValueChange = { reviewText.value = it },
                label = { Text("Add detailed review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Photo Section
            Button(onClick = {
                // Logic for adding photo (via intent or file picker)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Add Photo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit and Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navBack() }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                    Text("Cancel")
                }
                Button(onClick = {
                    viewModel.submitReview(order.id, reviewText.value, rating.value) { success ->
                        if (success) navBack()
                    }
                }) {
                    Text("Submit")
                }
            }
        }
    }
}
