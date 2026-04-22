package com.example.prova1_api_catdog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.prova1_api_catdog.core.RetrofitInstance
import com.example.prova1_api_catdog.data.repository.BreedRepository
import com.example.prova1_api_catdog.presentation.viewmodel.BreedsViewModel
import com.example.prova1_api_catdog.ui.theme.Prova1_API_catdogTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request


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
    // ========== PARTE 1: APP DE IMAGENS (já existente) ==========
    var selectedPet by remember { mutableStateOf("cat") }
    var imageUiState by remember { mutableStateOf<UiState>(UiState.Loading) }
    val coroutineScope = rememberCoroutineScope()

    fun fetchPets() {
        coroutineScope.launch {
            imageUiState = UiState.Loading
            val result = fetchPetImages(selectedPet)
            imageUiState = if (result.isNullOrEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(result)
            }
        }
    }

    LaunchedEffect(selectedPet) {
        fetchPets()
    }

    // ========== PARTE 2: BUSCA DE RAÇAS (nova) ==========
    val repository = BreedRepository(RetrofitInstance.apiService)
    val viewModel: BreedsViewModel = viewModel(
        factory = BreedsViewModel.Factory(repository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PetSearchApp - Busca de Pets e Raças") }
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
            // Seção de busca de raças (NOVA)
            Text(
                text = "🔍 Busca de Raças",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Digite o nome da raça") },
                placeholder = { Text("Ex: Bengal, Siamese, Persian...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estados da busca de raças
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscando raças...")
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("❌ ${uiState.errorMessage}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.clearError() }) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }
                uiState.isEmptyResult && !uiState.isFirstLoad -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("😢 Nenhuma raça encontrada para \"$searchQuery\"")
                            Text("Tente outro nome de raça", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                uiState.isFirstLoad -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍 Digite o nome de uma raça para buscar")
                            Text("Exemplos: Bengal, Siamese, Persian, Maine Coon",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                uiState.breeds.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.breeds) { breed ->
                            BreedCard(breed = breed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Seção de imagens de pets (já existente)
            Text(
                text = "🐾 Fotos Aleatórias",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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

            when (val state = imageUiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text("Carregando...", modifier = Modifier.padding(top = 8.dp))
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
                        modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.fillMaxWidth(),
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
fun BreedCard(breed: com.example.prova1_api_catdog.domain.model.Breed) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = breed.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (breed.origin.isNotEmpty()) {
                Text(
                    text = "🌍 Origem: ${breed.origin}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (breed.temperament.isNotEmpty()) {
                Text(
                    text = "🎭 Temperamento: ${breed.temperament}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (breed.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = breed.description.take(100) + if (breed.description.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodySmall
                )
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
            androidx.compose.foundation.Image(
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