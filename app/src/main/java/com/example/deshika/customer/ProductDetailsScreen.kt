package com.example.deshika.customer

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deshika.admin.AdminViewModel
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProductDetailsScreen(
    productId: String,
    viewModel: AdminViewModel,
    client: io.appwrite.Client,
    navController: NavController
) {
    val productList by viewModel.productList.observeAsState(emptyList())
    val product = productList.find { it.id == productId }
    val storage = Storage(client)
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    LaunchedEffect(productId) {
        if (product == null) {
            viewModel.fetchProducts()
        }
    }

    LaunchedEffect(product?.imageId) {
        isLoading = true
        try {
            product?.imageId?.let {
                val result = withContext(Dispatchers.IO) {
                    storage.getFileDownload(bucketId = "678bd18e0013db3bbfd8", fileId = it)
                }
                imageData = result
            }
        } catch (e: Exception) {
            imageData = null
        } finally {
            isLoading = false
        }
    }

    product?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    imageData?.let {
                        Image(
                            bitmap = BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap(),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                }
            }
            item {
                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            item {
                Text(
                    text = product.description,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            item {
                Text(
                    text = "Size: ${product.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            item {
                Text(
                    text = "Price: ${product.price} TK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.addToCart(product)
                            navController.navigate("cart")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Text("Add to Cart")
                    }
                    Button(
                        onClick = {
                            navController.navigate("orderConfirmation/${product.id}/${product.price}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Text("Order Now")
                    }
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Product not found", style = MaterialTheme.typography.headlineSmall)
    }
}
