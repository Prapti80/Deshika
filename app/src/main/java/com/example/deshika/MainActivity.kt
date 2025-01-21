package com.example.deshika


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deshika.Log.ForgotPasswordScreen
import com.example.deshika.Log.LoginScreen
import com.example.deshika.Log.RegisterScreen
import com.example.deshika.admin.AdminHomeScreen
import com.example.deshika.admin.AdminViewModel


import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Permissions Denied. Some features may not work.", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var viewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase and ViewModel
        FirebaseApp.initializeApp(this)
        viewModel = CustomerViewModel(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        // Check and request permissions
        if (!hasPermissions()) {
            requestPermissions()
        } else {
            fetchLocation()
            }
            val firestore = FirebaseFirestore.getInstance()
        val client = Client(this).setProject("678bc74400275005d6ad")
        setContent {
            AppNavigation(viewModel = viewModel,    client = client, firestore= firestore)
        }
    }

    private fun hasPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissionsLauncher.launch(requiredPermissions)
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val locationText = "${location.latitude}, ${location.longitude}"
                viewModel.updateLocation(locationText)
            } else {
                viewModel.updateLocation("Location unavailable")
            }
        }.addOnFailureListener {
            viewModel.updateLocation("Failed to fetch location")
        }
    }
}