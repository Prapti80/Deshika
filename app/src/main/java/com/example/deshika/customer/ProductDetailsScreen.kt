package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.deshika.customer.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    navBack: () -> Unit,
    addToCart: (Product) -> Unit
) {
    var selectedSize by remember { mutableStateOf("M") }
    var selectedColor by remember { mutableStateOf(Color(0xFFA1887F)) } // Default Brown

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = { navBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Main Product Image
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Product Images Carousel
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(5) { // Replace with dynamic images
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Product Information
            Text("Category: ${product.category}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rating: 4.5", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Product Description
            ClickableText(
                text = androidx.compose.ui.text.AnnotatedString(product.description),
                onClick = { /* Handle expand/collapse */ },
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Select Size
            Text("Select Size:", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("S", "M", "L", "XL", "XXL").forEach { size ->
                    Button(
                        onClick = { selectedSize = size },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSize == size) Color(0xFF795548) else Color.LightGray
                        )
                    ) {
                        Text(size, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Select Color
            Text("Select Color:", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val colors = listOf(
                    Color(0xFFA1887F), // Brown
                    Color(0xFFBCAAA4), // Beige
                    Color(0xFF546E7A), // Blue Gray
                    Color.Black
                )
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = color },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == color) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Total Price and Add to Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Price: $${product.price}", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { addToCart(product) }) {
                    Text("Add to Cart")
                }
            }
        }
    }
}
