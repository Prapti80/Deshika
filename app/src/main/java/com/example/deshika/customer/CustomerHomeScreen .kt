package com.example.deshika.customer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.Product
import io.appwrite.Client
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AdminViewModel,
    client: Client,
    bucketId: String = "678bd18e0013db3bbfd8"
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val productList by viewModel.productList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val storage = Storage(client)

    LaunchedEffect(true) {
        viewModel.fetchProducts()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        SearchBar(searchQuery) { query -> searchQuery = query }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val filteredProducts = productList.filter {
                it.name.contains(searchQuery.text, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(product = product, storage = storage, bucketId = bucketId) {
                        navController.navigate("productDetails/${product.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, storage: Storage, bucketId: String, onClick: () -> Unit) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(product.imageId) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId = bucketId, fileId = product.imageId)
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
            .wrapContentHeight()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
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
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price: ${product.price} TK",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}
