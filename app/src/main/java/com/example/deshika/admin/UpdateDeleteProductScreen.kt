package com.example.deshika.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.widget.Toast
import android.net.Uri
import android.content.Context
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun UpdateDeleteProductScreen(viewModel: AdminViewModel = viewModel(), client: Client) {
    var searchQuery by remember { mutableStateOf("") }
    val productList by viewModel.productList.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Product") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { viewModel.fetchProducts() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        productList.filter { it.name.contains(searchQuery, ignoreCase = true) }.forEach { product ->
            ProductItem(product, viewModel, client)
        }
    }
}

@Composable
fun ProductItem(product: Product, viewModel: AdminViewModel, client: Client) {
    val context = LocalContext.current
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val storage = Storage(client)

    LaunchedEffect(product.imageId) {
        imageData = withContext(Dispatchers.IO) {
            try {
                storage.getFileDownload(bucketId = "678bd18e0013db3bbfd8", fileId = product.imageId)
            } catch (e: Exception) {
                null
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            imageData?.let {
                Image(
                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
            Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Price: ${product.price}", style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    try {
                        viewModel.deleteProduct(product.id, {
                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        }, { error ->
                            println("Delete error: $error")
                        })
                    } catch (e: Exception) {
                        println("Exception during delete: ${e.message}")
                    }
                }) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showDialog = true }) {
                    Text(text = "Update")
                }
            }
        }
    }

    if (showDialog) {
        ShowUpdateDialog(product, viewModel, onDismiss = { showDialog = false }, context = context)
    }
}
@Composable
fun ShowUpdateDialog(product: Product, viewModel: AdminViewModel, onDismiss: () -> Unit, context: Context) {
    var updatedName by remember { mutableStateOf(product.name) }
    var updatedPrice by remember { mutableStateOf(product.price.toString()) }
    var updatedDescription by remember { mutableStateOf(product.description) }
    var updatedSize by remember { mutableStateOf(product.size) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
            imageFile = tempFile
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Update Product") },
        text = {
            Column {
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Product Name") }
                )
                OutlinedTextField(
                    value = updatedPrice,
                    onValueChange = { updatedPrice = it },
                    label = { Text("Price") }
                )
                OutlinedTextField(
                    value = updatedDescription,
                    onValueChange = { updatedDescription = it },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = updatedSize,
                    onValueChange = { updatedSize = it },
                    label = { Text("Size") }
                )
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Pick Image")
                }
                imageFile?.let {
                    Text("Image selected: ${it.name}")
                }
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                isUploading = true
                if (imageFile != null) {
                    viewModel.uploadProductImage(
                        imageFile!!,
                        onSuccess = { newImageId ->
                            val updatedProduct = mapOf(
                                "name" to updatedName,
                                "price" to updatedPrice.toDouble(),
                                "description" to updatedDescription,
                                "size" to updatedSize,
                                "imageId" to newImageId
                            )
                            viewModel.updateProduct(product.id, updatedProduct, {
                                Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                                isUploading = false
                                onDismiss()
                            }, { error ->
                                Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                                isUploading = false
                            })
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "Image upload failed: $error", Toast.LENGTH_SHORT).show()
                            isUploading = false
                        }
                    )
                } else {
                    val updatedProduct = mapOf(
                        "name" to updatedName,
                        "price" to updatedPrice.toDouble(),
                        "description" to updatedDescription,
                        "size" to updatedSize
                    )
                    viewModel.updateProduct(product.id, updatedProduct, {
                        Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                        isUploading = false
                        onDismiss()
                    }, { error ->
                        Toast.makeText(context, "Update failed: $error", Toast.LENGTH_SHORT).show()
                        isUploading = false
                    })
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}