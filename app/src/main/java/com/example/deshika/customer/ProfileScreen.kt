package com.example.deshika.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.deshika.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CustomerViewModel,
    navToSettings: () -> Unit,
    navToOrders: () -> Unit,
    navToPaymentMethods: () -> Unit,
    navToHelpCenter: () -> Unit,
    navToPrivacyPolicy: () -> Unit,
    navToLogoutConfirmation: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val updatedName = remember { mutableStateOf(user?.name ?: "") }
    val updatedEmail = remember { mutableStateOf(user?.email ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // User Profile Section
            user?.let {
                // Profile Picture
                Image(
                    painter = if (it.profilePictureUrl.isNotBlank()) {
                        rememberAsyncImagePainter(it.profilePictureUrl)
                    } else {
                        painterResource(R.drawable.ic_placeholder) // Placeholder icon
                    },
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            // Add logic to open image picker and update profile picture
                            // Example: viewModel.updateProfilePicture(newUri)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Editable Fields for Name and Email
                OutlinedTextField(
                    value = updatedName.value,
                    onValueChange = { updatedName.value = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = updatedEmail.value,
                    onValueChange = { updatedEmail.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        viewModel.updateUserProfile(
                            name = updatedName.value,
                            email = updatedEmail.value,
                            profilePictureUrl = user?.profilePictureUrl ?: ""
                        ) { success ->
                            if (success) {
                                // Notify the user about successful update
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Options
            ProfileOptionItem("Your Profile") { /* Action for Profile Details */ }
            ProfileOptionItem("Payment Methods", navToPaymentMethods)
            ProfileOptionItem("My Orders", navToOrders)
            ProfileOptionItem("Settings", navToSettings)
            ProfileOptionItem("Help Center", navToHelpCenter)
            ProfileOptionItem("Privacy Policy", navToPrivacyPolicy)
            ProfileOptionItem("Log Out", navToLogoutConfirmation)
        }
    }
}

@Composable
fun ProfileOptionItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
    }
}
