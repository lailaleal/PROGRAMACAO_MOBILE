package com.example.prova1_api_catdog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.prova1_api_catdog.ui.theme.Prova1_API_catdogTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Serializable
data class PetImage(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

sealed class UiState {
    object Loading : UiState()
    data class Success(val images: List<PetImage>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prova1_API_catdogTheme {
                PetSearchApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetSearchApp() {
    var selectedPet by remember { mutableStateOf("cat") }
    var uiState by remember { mutableStateOf<UiState>(UiState.Loading) }
    val coroutineScope = rememberCoroutineScope()

    fun fetchPets() {
        coroutineScope.launch {
            uiState = UiState.Loading
            val result = fetchPetImages(selectedPet)
            uiState = if (result.isNullOrEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(result)
            }
        }
    }

    LaunchedEffect(selectedPet) {
        fetchPets()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PetSearchApp - Busca de Pets") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = selectedPet == "cat",
                    onClick = { selectedPet = "cat" },
                    label = { Text("🐱 Gatos") }
                )
                FilterChip(
                    selected = selectedPet == "dog",
                    onClick = { selectedPet = "dog" },
                    label = { Text("🐶 Cachorros") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text("Carregando...", modifier = Modifier.padding(top = 60.dp))
                    }
                }
                is UiState.Success -> {
                    LazyColumn {
                        items(state.images) { pet ->
                            PetCard(pet)
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("❌ Erro: ${state.message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { fetchPets() }) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }
                is UiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("😢 Nenhum pet encontrado. Tente novamente.")
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: PetImage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(pet.url),
                contentDescription = "Pet",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                text = "ID: ${pet.id}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

suspend fun fetchPetImages(type: String): List<PetImage>? {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = if (type == "cat") {
                "https://api.thecatapi.com/v1/images/search?limit=10"
            } else {
                "https://api.thedogapi.com/v1/images/search?limit=10"
            }

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<List<PetImage>>(body)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPetSearchApp() {
    Prova1_API_catdogTheme {
        PetSearchApp()
    }
}