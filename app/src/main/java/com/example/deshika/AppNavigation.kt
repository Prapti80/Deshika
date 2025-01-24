package com.example.deshika

import OrderConfirmationScreen
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deshika.Log.*
import com.example.deshika.admin.*
import com.example.deshika.customer.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(
    viewModel: AdminViewModel,
    client: Client,
    firestore: FirebaseFirestore,
    context: Context
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                navController = navController,
                context = context
            )
        }
        composable("login") {
            LoginScreen(
                navController = navController, context = context,
                auth  = FirebaseAuth.getInstance()
            )
        }
        composable("register") {
            RegisterScreen(navController = navController, auth = FirebaseAuth.getInstance())
        }

        composable("adminHome") {
            AdminHomeScreen(
                navToUpload = { navController.navigate("uploadProduct") },
                navToDelete = { navController.navigate("deleteProduct") },
                navToShow = { navController.navigate("showProduct") },
                navToUpdate = { navController.navigate("showOrder") },
                navToLogin = {
                    FirebaseAuth.getInstance().signOut()
                    clearUserRole(context)
                    navController.navigate("login") {
                        popUpTo("adminHome") { inclusive = true }
                    }
                }
            )
        }

        composable("uploadProduct") {
            UploadScreen(viewModel = AdminViewModel(client, firestore, context))
        }

        composable("showProduct") {
            ShowProductsScreen(viewModel = AdminViewModel(client, firestore, context), client, bucketId = "678bd18e0013db3bbfd8")
        }

        composable("deleteProduct") {
            UpdateDeleteProductScreen(AdminViewModel(client, firestore, context), client)
        }

        composable("showOrder") {
            AdminOrdersScreen(navController = navController, firestore)
        }

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
                DisplayErrorMessage(navController)
            }
        }

        composable("cart") {
            CartScreen(viewModel = viewModel, navController = navController, client = client, context)
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

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("orderHistory") {
            MyOrdersScreen(navController = navController, firestore = firestore)
        }
    }
}

@Composable
fun DisplayErrorMessage(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Product not found", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}

// Function to clear stored role from SharedPreferences
fun clearUserRole(context: Context) {
    val sharedPreferences = context.getSharedPreferences("DeshikaPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("userRole").apply()
}
