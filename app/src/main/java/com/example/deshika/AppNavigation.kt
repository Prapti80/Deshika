package com.example.deshika


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.deshika.customer.OrderConfirmationScreen
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
            if (productId != null) {
                ProductDetailsScreen(productId = productId, viewModel = viewModel, client = client)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Product not found", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }



        composable("cart") { CartScreen(AdminViewModel(client, firestore), navController) }
        composable("orderConfirmation") { OrderConfirmationScreen(navController) }
        composable("profile") { ProfileScreen(AdminViewModel(client, firestore), navController) }
    }
}






