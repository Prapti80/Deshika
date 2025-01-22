package com.example.deshika


import OrderConfirmationScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deshika.Log.ForgotPasswordScreen
import com.example.deshika.Log.LoginScreen
import com.example.deshika.Log.RegisterScreen
import com.example.deshika.admin.AdminHomeScreen
import com.example.deshika.admin.ShowProductsScreen
import com.example.deshika.admin.UpdateDeleteProductScreen
import com.example.deshika.admin.UploadScreen

import com.example.deshika.customer.ProductDetailsScreen
import com.example.deshika.customer.ProfileScreen
import com.example.deshika.customer.SearchScreen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.customer.CartScreen
import com.example.deshika.customer.HomeScreen
import io.appwrite.services.Storage


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(viewModel: AdminViewModel,
                  client: Client, firestore: FirebaseFirestore
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, auth = FirebaseAuth.getInstance())
        }
        composable("register") {
            RegisterScreen(navController = navController, auth = FirebaseAuth.getInstance())
        }

        composable("adminHome") {
            AdminHomeScreen(
                navToUpload = { navController.navigate("uploadProduct") },
                navToDelete = { navController.navigate("deleteProduct") },
                navToShow = { navController.navigate("showProduct") },
                navToUpdate = { navController.navigate("updateProduct") }
            )
        }

        // Upload Product Screen
        composable("uploadProduct") {

            UploadScreen(
                viewModel = AdminViewModel(client, firestore)
            )
        }


        // Show Product Screen// Show Product Screen
        composable("showProduct") {

            ShowProductsScreen(
                viewModel = AdminViewModel(client, firestore),
                client = client,
                bucketId = "678bd18e0013db3bbfd8"
            )
        }
        composable("deleteProduct") {
            UpdateDeleteProductScreen(AdminViewModel(client, firestore), client)
        }

//        composable("updateProduct") {
//            UpdateProductScreen(AdminViewModel(client,firestore))
//        }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController, auth = FirebaseAuth.getInstance())
        }
        composable("home") {
            HomeScreen(navController, viewModel, client)
        }

        composable("productDetails/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")

            if (!productId.isNullOrEmpty()) {
                ProductDetailsScreen(
                    productId = productId,
                    viewModel = viewModel,
                    client = client,
                    navController = navController
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Product not found",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }


        composable("cart") {
            CartScreen(
                viewModel = viewModel, navController = navController,
                client = client
            )
        }


        composable("orderConfirmation/{productId}/{productPrice}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val productPrice = backStackEntry.arguments?.getString("productPrice")?.toDoubleOrNull()

            if (productId != null && productPrice != null) {
                OrderConfirmationScreen(navController, firestore, productPrice)
            } else {
                Text("Invalid product details")
            }
        }

        composable("profile") { ProfileScreen(AdminViewModel(client, firestore), navController) }
    }
}






