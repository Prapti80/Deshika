package com.example.deshika.admin

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.io.File


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(viewModel: AdminViewModel) {
    val productName = remember { mutableStateOf(TextFieldValue()) }
    val productDescription = remember { mutableStateOf(TextFieldValue()) }
    val productPrice = remember { mutableStateOf(TextFieldValue()) }
    val productSize = remember { mutableStateOf(TextFieldValue()) }
    val categories = listOf("Saree", "Kamij", "T-shirt", "Tops")
    val selectedCategory = remember { mutableStateOf(categories[0]) }
    val imageFile = remember { mutableStateOf<File?>(null) }
    val imageBitmap = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val uploadStatus = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
            if (tempFile.exists() && tempFile.length() > 0) {
                imageFile.value = tempFile
                imageBitmap.value = android.graphics.BitmapFactory.decodeFile(tempFile.absolutePath)
            } else {
                uploadStatus.value = "Invalid image file. Please select a valid image."
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Panel - Manage Products") }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))
            DropdownCategory(categories, selectedCategory)

            Spacer(modifier = Modifier.height(16.dp))

            ProductInputField("Product Name", productName)
            ProductInputField("Product Description", productDescription)
            ProductInputField("Product Price", productPrice)
            ProductInputField("Product Size", productSize)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text("Pick and Upload Image")
            }

            imageBitmap.value?.let { bitmap ->
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.foundation.Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.height(150.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (productName.value.text.isBlank() || productDescription.value.text.isBlank() ||
                        productPrice.value.text.isBlank() || productSize.value.text.isBlank() || imageFile.value == null
                    ) {
                        uploadStatus.value = "Please fill in all fields and upload an image."
                        return@Button
                    }

                    uploadStatus.value = "Uploading product..."
                    viewModel.uploadProduct(
                        name = productName.value.text,
                        description = productDescription.value.text,
                        price = productPrice.value.text,
                        size = productSize.value.text,
                        category = selectedCategory.value,
                        imageFile = imageFile.value!!,
                        onSuccess = { uploadStatus.value = "Product uploaded successfully!" },
                        onFailure = { error -> uploadStatus.value = "Upload failed: $error" }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Product")
            }

            Spacer(modifier = Modifier.height(16.dp))

            uploadStatus.value?.let { status ->
                Text(status, color = if (status.contains("successfully")) Color.Green else Color.Red)
            }
        }
    }
}
@Composable
fun DropdownCategory(categories: List<String>, selectedCategory: MutableState<String>) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
        Text(selectedCategory.value)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        categories.forEach { category ->
            DropdownMenuItem(
                text = { Text(category) },
                onClick = {
                    selectedCategory.value = category
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun ProductInputField(label: String, value: MutableState<TextFieldValue>) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}
