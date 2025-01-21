package com.example.deshika.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deshika.Log.CustomStyledTextField
import com.example.deshika.R

import com.google.firebase.auth.FirebaseAuth


@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
    val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex() // Minimum 6 characters, at least one letter and one number

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

        CustomStyledTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = it.isBlank()
            },
            label = "Confirm Password",
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            showError = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = email.isBlank()
                passwordError = password.isBlank()
                confirmPasswordError = confirmPassword.isBlank()

                if (!emailError && !passwordError && !confirmPasswordError) {
                    when {
                        !emailRegex.matches(email) -> {
                            errorMessage = "Please enter a valid email address"
                        }
                        !passwordRegex.matches(password) -> {
                            errorMessage = "Password must be at least 6 characters long, and include both letters and numbers"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Passwords do not match"
                        }
                        else -> {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                                            if (emailTask.isSuccessful) {
                                                Toast.makeText(
                                                    navController.context,
                                                    "Registration successful. Check your email for verification.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate("login")
                                            } else {
                                                errorMessage = "Failed to send verification email"
                                            }
                                        }
                                    } else {
                                        errorMessage = task.exception?.message ?: "Registration failed"
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
            Text("Register", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login", color = Color(0xFF8C6239))
        }
    }
}
