package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.deshika.R
import com.example.deshika.admin.AdminViewModel

@Composable
fun ProfileScreen(viewModel: AdminViewModel, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "My Profile", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { /* Logout Logic */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "My Orders", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(viewModel.productList.value ?: emptyList()) { order ->
                Text("Order: ${order.name} - ${order.price} TK")
            }
        }
    }
}
