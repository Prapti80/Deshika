package com.example.deshika


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.deshika.admin.AdminViewModel


import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.deshika.customer.CustomerViewModel


import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val firestore = FirebaseFirestore.getInstance()
        val client = Client(this).setProject("678bc74400275005d6ad")

        setContent {
            AppNavigation(
                viewModel = AdminViewModel(client, firestore, applicationContext),
                client = client,
                firestore = firestore,
                context = applicationContext,
            )}
    }



}