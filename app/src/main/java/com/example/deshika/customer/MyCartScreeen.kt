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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.Product
import com.example.deshika.admin.ProductItem

@Composable
fun CartScreen(viewModel: AdminViewModel, navController: NavController) {
    val cartItems = remember { mutableStateListOf<Product>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "My Cart", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(cartItems) { product ->
                ProductItem(
                    product, viewModel = TODO(),
                    client = TODO()
                )
            }
        }
        Button(
            onClick = { navController.navigate("orderConfirmation") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Checkout")
        }
    }
}