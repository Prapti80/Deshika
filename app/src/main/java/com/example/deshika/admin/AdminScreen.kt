package com.example.deshika.admin

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navToUpload: () -> Unit,
    navToDelete: () -> Unit,
    navToShow: () -> Unit,
    navToUpdate: () -> Unit,
    navToLogin: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", textAlign = TextAlign.Center) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8C6239),
                    titleContentColor = Color.White
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AdminButton("Upload Product", onClick = navToUpload)
                    AdminButton("Delete & Update Product", onClick = navToDelete)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AdminButton("Show Product", onClick = navToShow)
                    AdminButton("Show Order", onClick = navToUpdate)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()

                    // Clear role data from SharedPreferences
                    clearUserRole(context)

                    // Navigate to login screen
                    navToLogin()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C6239)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Log Out", textAlign = TextAlign.Center, color = Color.White)
            }
        }
    }
}

@Composable
fun AdminButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C6239))
    ) {
        Text(text, textAlign = TextAlign.Center, color = Color.White)
    }
}

// Function to clear stored role from SharedPreferences
fun clearUserRole(context: Context) {
    val sharedPreferences = context.getSharedPreferences("DeshikaPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("userRole").apply()
}
