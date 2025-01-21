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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.deshika.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: CustomerViewModel,
    navToProductDetails: (Product) -> Unit,
    navToProfile: () -> Unit,
    navToWishlist: () -> Unit,
    navToCart: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val user by viewModel.user.collectAsState()
    val location by viewModel.location.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(location.ifEmpty { "Fetching location..." })
                    }
                },
                actions = {
                    IconButton(onClick = navToProfile) {
                        if (user?.profilePictureUrl?.isNotBlank() == true) {
                            Image(
                                painter = rememberAsyncImagePainter(user?.profilePictureUrl),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(painter = painterResource(R.drawable.ic_user), contentDescription = "Profile")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { /* Already Home */ }) {
                        Icon(painterResource(R.drawable.ic_home), contentDescription = "Home")
                    }
                    IconButton(onClick = navToWishlist) {
                        Icon(painterResource(R.drawable.ic_heart), contentDescription = "Wishlist")
                    }
                    IconButton(onClick = navToCart) {
                        Icon(painterResource(R.drawable.ic_cart), contentDescription = "Cart")
                    }
                    IconButton(onClick = navToProfile) {
                        Icon(painterResource(R.drawable.ic_user), contentDescription = "Profile")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Collection Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "New Collection\nDiscount 50% for the first transaction",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            Text("Categories", style = MaterialTheme.typography.headlineSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(viewModel.predefinedCategories) { category ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { /* Handle Category Filter */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = category, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Grid
            Text("Flash Sale", style = MaterialTheme.typography.headlineSmall)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(products) { product ->
                    ProductCard(product = product) { navToProductDetails(product) }
                }
            }
        }
    }
}
