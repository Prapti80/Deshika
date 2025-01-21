package com.example.deshika.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.ProductItem

@Composable
fun SearchScreen(viewModel: AdminViewModel,navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val productList by viewModel.productList.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search products") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(productList.filter { it.name.contains(searchQuery, ignoreCase = true) }) { product ->
             //   ProductItem(product) { navController.navigate("productDetails/${product.id}") }
            }
        }
    }
}