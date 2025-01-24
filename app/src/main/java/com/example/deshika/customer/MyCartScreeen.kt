package com.example.deshika.customer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.Product
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CartScreen(viewModel: AdminViewModel, navController: NavController, client: Client, context: Context) {
    val cartItems by viewModel.cartItems.observeAsState(emptyList())

    // Load cart items when the screen is opened
    LaunchedEffect(Unit) {
        viewModel.loadCartFromStorage()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "My Cart", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { product ->
                    CartItem(
                        product = product,
                        client = client,
                        onDelete = {
                            viewModel.removeFromCart(product)
                        },
                        onOrderNow = {
                            navController.navigate("orderConfirmation/${product.id}/${product.price}")
                        },
                        onClick = {
                            navController.navigate("productDetails/${product.id}")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("orderConfirmation/0.0") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
fun CartItem(
    product: Product,
    client: Client,
    onDelete: () -> Unit,
    onOrderNow: () -> Unit,
    onClick: () -> Unit
) {
    val storage = Storage(client)
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(product.imageId) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId = "678bd18e0013db3bbfd8", fileId = product.imageId)
            }
            imageData = result
        } catch (e: Exception) {
            imageData = null
        } finally {
            isLoading = false
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                imageData?.let {
                    Image(
                        bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Price: ${product.price} TK",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
                Button(
                    onClick = onOrderNow,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Order Now")
                }
            }
        }
    }
}


