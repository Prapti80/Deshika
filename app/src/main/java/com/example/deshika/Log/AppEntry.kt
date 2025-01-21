package com.example.deshika.Log

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppEntry(navController: NavController, auth: FirebaseAuth) {
    val user = remember { auth.currentUser }

    LaunchedEffect(user) {
        if (user != null) {
            // User is already logged in, redirect to Home screen
            navController.navigate("homeScreen") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            // User is not logged in, redirect to Login screen
            navController.navigate("login") {
                popUpTo("homeScreen") { inclusive = true }
            }
        }
    }
}
