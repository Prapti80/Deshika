import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderConfirmationScreen(navController: NavController, firestore: FirebaseFirestore, productPrice: Double) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }
    var transactionId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val deliveryCharge = if (location.lowercase().contains("sylhet")) 60 else 150
    val totalAmount = (productPrice * quantity) + deliveryCharge

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Order Confirmation",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = size,
                onValueChange = { size = it },
                label = { Text("Size") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Quantity", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(50.dp))
                Button(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { quantity++ }) { Text("+") }
                Spacer(modifier = Modifier.width(50.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Bill Summary", style = MaterialTheme.typography.headlineSmall)
                    Text("Cloth Price: ${productPrice * quantity} TK", style = MaterialTheme.typography.bodyLarge)
                    Text("Delivery Charge: $deliveryCharge TK", style = MaterialTheme.typography.bodyLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Total Amount: $totalAmount TK", style = MaterialTheme.typography.headlineMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Select Payment Method:", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = paymentMethod == "COD", onClick = { paymentMethod = "COD" })
                Text(text = "Cash on Delivery", modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = paymentMethod == "Bkash", onClick = { paymentMethod = "Bkash" })
                Text(text = "Bkash", modifier = Modifier.padding(start = 8.dp))
            }

            if (paymentMethod == "Bkash") {
                OutlinedTextField(
                    value = transactionId,
                    onValueChange = { transactionId = it },
                    label = { Text("Bkash Transaction ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Delivery Information Text
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You will receive your parcel within 2-3 working days. Please check it in the presence of the delivery man.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    confirmOrder(
                        name, phone, location, size, quantity, paymentMethod, transactionId, deliveryCharge, totalAmount, firestore
                    )
                    Toast.makeText(context, "Order Confirmed Successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("home")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotEmpty() && phone.isNotEmpty() && location.isNotEmpty() && size.isNotEmpty() &&
                        (paymentMethod == "COD" || (paymentMethod == "Bkash" && transactionId.isNotEmpty()))
            ) {
                Text("Confirm Order")
            }
        }
    }
}

fun confirmOrder(
    name: String,
    phone: String,
    location: String,
    size: String,
    quantity: Int,
    paymentMethod: String,
    transactionId: String,
    deliveryCharge: Int,
    totalAmount: Double,
    firestore: FirebaseFirestore
) {
    val orderId = firestore.collection("orders").document().id  // Generate unique order ID
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"

    val order = hashMapOf(
        "orderId" to orderId,
        "userId" to userId,   // Adding userId field
        "name" to name,
        "phone" to phone,
        "location" to location,
        "size" to size,
        "quantity" to quantity,
        "paymentMethod" to paymentMethod,
        "transactionId" to if (paymentMethod == "Bkash") transactionId else "",
        "deliveryCharge" to deliveryCharge,
        "totalAmount" to totalAmount,
        "status" to "Pending",
        "timestamp" to System.currentTimeMillis()
    )

    firestore.collection("orders")
        .document(orderId)
        .set(order)
        .addOnSuccessListener {
            println("Order placed successfully with ID: $orderId")
        }
        .addOnFailureListener {
            println("Failed to place order: ${it.message}")
        }
}
