package com.example.deshika.Log

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.deshika.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth, context: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val sharedPreferences = context.getSharedPreferences("DeshikaPrefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val savedRole = sharedPreferences.getString("userRole", null)
        if (savedRole == "admin") {
            navController.navigate("adminHome") {
                popUpTo("login") { inclusive = true }
            }
        } else if (savedRole == "customer") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
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
        Image(
            painter = painterResource(id = R.drawable.logo),  // Replace 'logo' with your actual image file name
            contentDescription = "App Logo",
            modifier = Modifier
                .size(250.dp)
        )
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
                    if (email.trim().lowercase() == "praptickb8@gmail.com" && password == "123456prapti") {
                        sharedPreferences.edit().putString("userRole", "admin").apply()
                        navController.navigate("adminHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Please enter a valid email address"
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && user.isEmailVerified) {
                                        sharedPreferences.edit().putString("userRole", "customer").apply()
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
