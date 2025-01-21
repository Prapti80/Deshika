package com.example.deshika


import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deshika.Log.ForgotPasswordScreen
import com.example.deshika.Log.LoginScreen
import com.example.deshika.Log.RegisterScreen
import com.example.deshika.admin.AdminHomeScreen
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.ShowProductsScreen
import com.example.deshika.admin.UpdateDeleteProductScreen
import com.example.deshika.admin.UploadScreen
import com.example.deshika.customer.CheckoutScreen
import com.example.deshika.customer.CustomerViewModel
import com.example.deshika.customer.HomePage
import com.example.deshika.customer.LeaveOrderReviewScreen
import com.example.deshika.customer.MyCartScreen
import com.example.deshika.customer.MyOrdersScreen
import com.example.deshika.customer.ProductDetailsScreen
import com.example.deshika.customer.ProfileScreen
import com.example.deshika.customer.SearchScreen
import com.example.deshika.customer.ShippingAddressScreen
import com.example.deshika.customer.WishlistScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(viewModel: CustomerViewModel,
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
                viewModel = AdminViewModel(client,firestore)
            )
        }




        // Show Product Screen// Show Product Screen
        composable("showProduct") {

            ShowProductsScreen(
                viewModel = AdminViewModel(client,firestore),
                client = client,
                bucketId = "678bd18e0013db3bbfd8"
            )
        }
        composable("deleteProduct") {
            UpdateDeleteProductScreen(AdminViewModel(client, firestore),client)
        }

//        composable("updateProduct") {
//            UpdateProductScreen(AdminViewModel(client,firestore))
//        }
        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController, auth = FirebaseAuth.getInstance())
        }

        // Home Page
        composable("home") {
            HomePage(
                viewModel = viewModel,
                navToProductDetails = { product ->
                    navController.navigate("productDetails/${product.id}")
                },
                navToProfile = {
                    navController.navigate("profile")
                },
                navToWishlist = {
                    navController.navigate("wishlist")
                },
                navToCart = {
                    navController.navigate("cart")
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                navToSettings = { /* Navigate to settings */ },
                navToOrders = { navController.navigate("orders") },
                navToPaymentMethods = { /* Navigate to payment methods */ },
                navToHelpCenter = { /* Navigate to help center */ },
                navToPrivacyPolicy = { /* Navigate to privacy policy */ },
                navToLogoutConfirmation = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("productDetails/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = viewModel.products.value.find { it.id == productId }
            product?.let {
                ProductDetailsScreen(
                    product = it,
                    navBack = { navController.popBackStack() },
                    addToCart = { viewModel.addToCart(it) }
                )
            }
        }
        composable("wishlist") {
            WishlistScreen(
                viewModel = viewModel,
                navToProductDetails = { product ->
                    navController.navigate("productDetails/${product.id}")
                }
            )
        }
        composable("cart") {
            MyCartScreen(
                viewModel = viewModel,
                navToCheckout = { navController.navigate("checkout") }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                viewModel = viewModel,
                navToShipping = { navController.navigate("shippingAddress") }
            )
        }
        composable("shippingAddress") {
            ShippingAddressScreen(navToCheckout = { navController.navigate("checkout") })
        }
        composable("orders") {
            MyOrdersScreen(
                viewModel = viewModel,
                navToReview = { order ->
                    navController.navigate("leaveReview/${order.id}")
                },
                navToReorder = { /* Handle reorder */ }
            )
        }
        composable("leaveReview/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val order = viewModel.orders.value.find { it.id == orderId }
            order?.let {
                LeaveOrderReviewScreen(
                    viewModel = viewModel,
                    order = it,
                    navBack = { navController.popBackStack() }
                )
            }
        }
        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                navToProductDetails = { product ->
                    navController.navigate("productDetails/${product.id}")
                }
            )
        }
    }
}







