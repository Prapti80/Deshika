package com.example.deshika.Log

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deshika.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, context: Context) {
    LaunchedEffect(Unit) {
        delay(2000)  // Delay for 2 seconds

        val sharedPreferences = context.getSharedPreferences("DeshikaPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPreferences.getString("userRole", null)

        if (FirebaseAuth.getInstance().currentUser != null) {
            when (userRole) {
                "admin" -> navController.navigate("adminHome") { popUpTo("splash") { inclusive = true } }
                "customer" -> navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                else -> navController.navigate("login") { popUpTo("splash") { inclusive = true } }
            }
        } else {
            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),  // Replace 'logo' with your actual image file name
            contentDescription = "App Logo",
            modifier = Modifier
                .size(250.dp)
        )
    }
}
