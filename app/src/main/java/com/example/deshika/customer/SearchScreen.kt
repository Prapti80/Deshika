package com.example.deshika.customer

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.deshika.customer.Product
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: CustomerViewModel, navToProductDetails: (Product) -> Unit) {
    val searchQuery = remember { mutableStateOf("") }
    val searchResults = viewModel.searchProducts(searchQuery.value).collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Load recent searches from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("recent_searches", Context.MODE_PRIVATE)
    val recentSearches = remember { mutableStateOf(sharedPreferences.getStringSet("searches", emptySet())!!.toMutableList()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { query ->
                    searchQuery.value = query
                    if (query.isNotBlank() && !recentSearches.value.contains(query)) {
                        recentSearches.value.add(query)
                        sharedPreferences.edit().putStringSet("searches", recentSearches.value.toSet()).apply()
                    }
                },
                label = { Text("Search for products...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Recent Searches
            if (searchQuery.value.isEmpty()) {
                Text("Recent Searches", style = MaterialTheme.typography.headlineSmall)
                LazyColumn {
                    items(recentSearches.value) { recent ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    searchQuery.value = recent
                                },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(recent, style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = {
                                recentSearches.value.remove(recent)
                                sharedPreferences.edit().putStringSet("searches", recentSearches.value.toSet()).apply()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Remove Recent")
                            }
                        }
                    }
                }
            } else {
                // Search Results
                Text("Results for \"${searchQuery.value}\"", style = MaterialTheme.typography.headlineSmall)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults.value) { product ->
                        ProductCard(product = product, onClick = { navToProductDetails(product) })
                    }
                }
            }
        }
    }
}
