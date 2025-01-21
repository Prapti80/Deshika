package com.example.deshika.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Product data model
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val size: String = "",
    val category: String = "",
    val imageUrl: String = ""
)

// Order data model
data class Order(
    val id: String = "",
    val total: Double = 0.0,
    val status: String = "Pending",
    val products: List<Product> = emptyList(),
    val review: String? = null,
    val rating: Int? = null
)

// User data model
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String = ""
)

class CustomerViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    // Product list
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // Cart
    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems

    // Orders
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    // User profile
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Predefined categories
    val predefinedCategories = listOf("Saree", "Kamij", "T-Shirt", "Top")



    private val _location = MutableStateFlow("Fetching location...")
    val location: StateFlow<String> = _location

    init {
        loadProducts()
        loadOrders()
        loadUserProfile()
    }

    // Load products
    private fun loadProducts() {
        viewModelScope.launch {
            firestore.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    val productList = result.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }.filter { predefinedCategories.contains(it.category) }
                    _products.value = productList
                }
                .addOnFailureListener { e ->
                    Log.e("CustomerViewModel", "Failed to load products: ${e.message}")
                }
        }
    }

    // Get products by category
    fun getProductsByCategory(category: String): StateFlow<List<Product>> {
        val filteredProducts = MutableStateFlow<List<Product>>(emptyList())
        viewModelScope.launch {
            filteredProducts.value = _products.value.filter { it.category.equals(category, ignoreCase = true) }
        }
        return filteredProducts
    }

    // Add product to cart
    fun addToCart(product: Product) {
        viewModelScope.launch {
            val updatedCart = _cartItems.value.toMutableList().apply { add(product) }
            _cartItems.value = updatedCart
        }
    }

    // Remove product from cart
    fun removeFromCart(product: Product) {
        viewModelScope.launch {
            val updatedCart = _cartItems.value.toMutableList().apply { remove(product) }
            _cartItems.value = updatedCart
        }
    }

    // Place an order
    fun placeOrder(onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false)
            return
        }

        val order = Order(
            id = firestore.collection("orders").document().id,
            total = _cartItems.value.sumOf { it.price },
            products = _cartItems.value
        )

        firestore.collection("orders")
            .document(order.id)
            .set(order)
            .addOnSuccessListener {
                _cartItems.value = emptyList() // Clear the cart
                loadOrders()
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("CustomerViewModel", "Failed to place order: ${e.message}")
                onComplete(false)
            }
    }

    // Load orders
    private fun loadOrders() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            firestore.collection("orders")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    val orderList = result.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)?.copy(id = doc.id)
                    }
                    _orders.value = orderList
                }
                .addOnFailureListener { e ->
                    Log.e("CustomerViewModel", "Failed to load orders: ${e.message}")
                }
        }
    }

    // Submit a review
    fun submitReview(orderId: String, review: String, rating: Int, onComplete: (Boolean) -> Unit) {
        if (rating !in 1..5 || review.isBlank()) {
            onComplete(false)
            return
        }
        viewModelScope.launch {
            firestore.collection("orders")
                .document(orderId)
                .update(mapOf("review" to review, "rating" to rating))
                .addOnSuccessListener {
                    loadOrders()
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e("CustomerViewModel", "Failed to submit review: ${e.message}")
                    onComplete(false)
                }
        }
    }
    private fun loadUserProfile() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val userProfile = doc.toObject(User::class.java)?.copy(id = doc.id)
                    _user.value = userProfile
                }
                .addOnFailureListener { e ->
                    Log.e("CustomerViewModel", "Failed to load user profile: ${e.message}")
                }
        }

    }


    // Update user profile
    fun updateUserProfile(name: String, email: String, profilePictureUrl: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser ?: return
        val updatedUser = User(
            id = currentUser.uid,
            name = name,
            email = email,
            profilePictureUrl = profilePictureUrl
        )
        firestore.collection("users")
            .document(currentUser.uid)
            .set(updatedUser)
            .addOnSuccessListener {
                _user.value = updatedUser
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("CustomerViewModel", "Failed to update profile: ${e.message}")
                onComplete(false)
            }
    }
    fun searchProducts(query: String): StateFlow<List<Product>> {
        val searchResults = MutableStateFlow<List<Product>>(emptyList())
        viewModelScope.launch {
            searchResults.value = _products.value.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }
        return searchResults
    }

    // For Wishlist
    private val _wishlistItems = MutableStateFlow<List<Product>>(emptyList())
    val wishlistItems: StateFlow<List<Product>> = _wishlistItems

    fun addToWishlist(product: Product) {
        viewModelScope.launch {
            _wishlistItems.value = _wishlistItems.value + product
        }
    }

    fun removeFromWishlist(product: Product) {
        viewModelScope.launch {
            _wishlistItems.value = _wishlistItems.value - product
        }
    }
    fun updateLocation(newLocation: String) {
        _location.value = newLocation
    }

}
