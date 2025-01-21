package com.example.deshika.admin

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navToUpload: () -> Unit,
    navToDelete: () -> Unit,
    navToShow: () -> Unit,
    navToUpdate: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Panel", textAlign = TextAlign.Center) }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                AdminButton("Upload Product", onClick = navToUpload)
                AdminButton("Delete Product", onClick = navToDelete)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                AdminButton("Show Product", onClick = navToShow)
                AdminButton("Update Product", onClick = navToUpdate)
            }
        }
    }
}

@Composable
fun AdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(120.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, textAlign = TextAlign.Center)
    }
}
