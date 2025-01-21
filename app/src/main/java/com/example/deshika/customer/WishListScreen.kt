package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(viewModel: CustomerViewModel, navToProductDetails: (Product) -> Unit) {
    val wishlistItems by viewModel.wishlistItems.collectAsState() // Using wishlistItems instead of cartItems
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Jacket", "Shirt", "Pant", "T-Shirt")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
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
            // Category Filter Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    Button(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == category) Color(0xFF795548) else Color.LightGray
                        ),
                        shape = CircleShape
                    ) {
                        Text(category, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Wishlist Items
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wishlistItems.filter { selectedCategory == "All" || it.category == selectedCategory }) { product ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(4.dp)
                            .clickable { navToProductDetails(product) },
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            // Product Image
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.imageUrl),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Wishlist Icon",
                                    tint = Color.Red,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Product Details
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${product.price} USD", color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("4.9", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Move to Cart Button
                            Button(
                                onClick = {
                                    viewModel.addToCart(product)
                                    viewModel.removeFromWishlist(product)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Move to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}
