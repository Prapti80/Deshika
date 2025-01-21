package com.example.deshika.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import com.example.deshika.R

import com.google.firebase.auth.FirebaseAuth
@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Check if user is already logged in
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            val email = currentUser.email
            if (email == "admin@example.com") { // Replace with your admin email
                navController.navigate("adminHome") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomStyledTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = it.isBlank()
            },
            label = "Email",
            showError = emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomStyledTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = it.isBlank()
            },
            label = "Password",
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible },
            showError = passwordError
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                emailError = email.isBlank()
                passwordError = password.isBlank()

                if (!emailError && !passwordError) {
                    when {
                        email == "admin" && password == "admin" -> {
                            navController.navigate("adminHome") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            errorMessage = "Please enter a valid email address"
                        }
                        else -> {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        if (user != null && user.isEmailVerified) {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            errorMessage = "Email is not verified. Please check your email."
                                            auth.signOut()
                                        }
                                    } else {
                                        errorMessage = task.exception?.message ?: "Login failed"
                                    }
                                }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8C6239),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("forgotPassword") }) {
            Text("Forgot Password?", color = Color(0xFF8C6239))
        }

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Create Account", color = Color(0xFF8C6239))
        }
    }
}