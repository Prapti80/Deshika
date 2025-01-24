package com.example.deshika.customer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.NavController
import com.example.deshika.admin.AdminViewModel
import com.example.deshika.admin.Product
import com.google.firebase.auth.FirebaseAuth
import io.appwrite.Client

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AdminViewModel,
    client: Client,
    bucketId: String = "678bd18e0013db3bbfd8"
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Saree", "Kamij", "T-shirt", "Tops")

    val productList by viewModel.productList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val storage = Storage(client)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.fetchProducts()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, context)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Deshika") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                if (showSearchBar) {
                    SearchBar(searchQuery) { query -> searchQuery = query }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Category Selection Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = category == selectedCategory,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val filteredProducts = productList.filter {
                        (selectedCategory == "All" || it.category == selectedCategory) &&
                                it.name.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductCard(product = product, storage = storage, bucketId = bucketId) {
                                navController.navigate("productDetails/${product.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        modifier = Modifier
            .padding(4.dp)
            .height(40.dp)
    ) {
        Text(text = category, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DrawerContent(navController: NavController, context: Context) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Home",
                tint = Color.Black
            )
        }
        Text(text = "Welcome!", style = MaterialTheme.typography.headlineSmall)

        user?.email?.let {
            Text(text = "Email: $it", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        } ?: Text(text = "Email: Not available", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Divider()

        DrawerItem(icon = Icons.Default.List, title = "My Orders") {
            navController.navigate("orderHistory")
        }

        DrawerItem(icon = Icons.Default.ShoppingCart, title = "My Cart") {
            navController.navigate("cart")
        }
        DrawerItem(icon = Icons.AutoMirrored.Filled.ExitToApp, title = "Logout") {
            FirebaseAuth.getInstance().signOut()
            clearUserRole(context)
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}

@Composable
fun DrawerItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProductCard(product: Product, storage: Storage, bucketId: String, onClick: () -> Unit) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }

    // Load product image asynchronously
    LaunchedEffect(product.imageId) {
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId = bucketId, fileId = product.imageId)
            }
            imageData = result
        } catch (e: Exception) {
            imageData = null
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imageData?.let {
                Image(
                    bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Price: ${product.price} TK",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
fun SearchBar(searchQuery: String, onSearch: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { onSearch(it) },
        label = { Text("Search products...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier.fillMaxWidth()
    )
}

fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}
fun clearUserRole(context: Context) {
    val sharedPreferences = context.getSharedPreferences("DeshikaPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("userRole").apply()
}