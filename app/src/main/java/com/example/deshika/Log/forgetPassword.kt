package com.example.deshika.Log


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.google.firebase.auth.FirebaseAuth

import androidx.compose.ui.graphics.Color

@Composable
fun ForgotPasswordScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

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
            label = "Enter your email",
            showError = emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = email.isBlank()

                if (!emailError) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                message = "Password reset email sent."
                            } else {
                                message = task.exception?.message ?: "Failed to send reset email"
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
            Text("Send Reset Email", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        message?.let {
            Text(text = it)
        }
    }
}
