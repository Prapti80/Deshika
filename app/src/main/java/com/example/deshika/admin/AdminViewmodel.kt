package com.example.deshika.admin

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.File
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage

class AdminViewModel(
    appwriteClient: Client,
    private val firestore: FirebaseFirestore,
    private val context: Context
) : ViewModel() {

    private val bucketId = "678bd18e0013db3bbfd8"
    private val storage = Storage(appwriteClient)
    private val sharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = _productList

    private val _cartItems = MutableLiveData<MutableList<Product>>(loadCartFromStorage())
    val cartItems: MutableLiveData<MutableList<Product>> get() = _cartItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value ?: mutableListOf()
        if (!currentCart.any { it.id == product.id }) {
            currentCart.add(product)
            _cartItems.value = currentCart
            saveCartToStorage(currentCart)
            Log.d("Cart", "Added product: ${product.name}")
        }
    }

    fun removeFromCart(product: Product) {
        val currentCart = _cartItems.value ?: mutableListOf()
        currentCart.removeIf { it.id == product.id }
        _cartItems.value = currentCart
        saveCartToStorage(currentCart)
        Log.d("Cart", "Removed product: ${product.name}")
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
        saveCartToStorage(mutableListOf())
        Log.d("Cart", "Cart cleared")
    }

    private fun saveCartToStorage(cartList: List<Product>) {
        val editor = sharedPreferences.edit()
        val jsonCart = gson.toJson(cartList)
        editor.putString("cart_items", jsonCart)
        editor.apply()
        Log.d("Cart", "Cart saved to storage: $jsonCart")
    }

    fun loadCartFromStorage(): MutableList<Product> {
        val jsonCart = sharedPreferences.getString("cart_items", null)
        return if (!jsonCart.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Product>>() {}.type
            gson.fromJson(jsonCart, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }

    fun uploadProduct(
        name: String,
        description: String,
        price: String,
        size: String,
        category: String,
        imageFile: File,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val fileId = uploadImageToAppwrite(imageFile)
                val priceValue = price.toDoubleOrNull()
                if (priceValue == null) {
                    onFailure("Invalid price format. Please enter a numeric value.")
                    return@launch
                }

                val productId = firestore.collection("products").document().id

                val product = mapOf(
                    "id" to productId,
                    "name" to name,
                    "description" to description,
                    "price" to priceValue,
                    "size" to size,
                    "category" to category,
                    "imageId" to fileId
                )

                firestore.collection("products").document(productId).set(product)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Failed to add product: ${e.message}")
                        onFailure("Firestore Error: ${e.message}")
                    }
            } catch (e: Exception) {
                onFailure("Upload Error: ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToAppwrite(file: File): String {
        return storage.createFile(
            bucketId = bucketId,
            fileId = ID.unique(),
            file = InputFile.fromFile(file)
        ).id
    }

    fun fetchProducts() {
        _isLoading.value = true
        firestore.collection("products")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(Product::class.java)
                }
                _productList.value = products
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                Log.e("Firestore", "Failed to fetch products: ${e.message}")
            }
    }

    fun updateProduct(
        productId: String,
        updatedProduct: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("products").document(productId).update(updatedProduct)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to update product: ${e.message}")
                onFailure("Firestore Error: ${e.message}")
            }
    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("products").document(productId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error deleting product") }
    }

    fun uploadProductImage(imageFile: File, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val fileId = storage.createFile(
                    bucketId = bucketId,
                    fileId = ID.unique(),
                    file = InputFile.fromFile(imageFile)
                ).id
                onSuccess(fileId)
            } catch (e: Exception) {
                onFailure(e.message ?: "Image upload failed")
            }
        }
    }
}

// Data class for Product
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val size: String = "",
    val category: String = "",
    val imageId: String = ""
)
