package com.example.prova1_api_catdog.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.prova1_api_catdog.core.RetrofitInstance
import com.example.prova1_api_catdog.data.repository.BreedRepository
import com.example.prova1_api_catdog.domain.model.Breed
import com.example.prova1_api_catdog.presentation.viewmodel.BreedsViewModel
import com.example.prova1_api_catdog.ui.theme.Prova1_API_catdogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var selectedPetType by remember { mutableStateOf("cat") }

    val repository = BreedRepository(
        RetrofitInstance.catApiService,
        RetrofitInstance.dogApiService
    )

    val viewModel: BreedsViewModel = viewModel(
        factory = BreedsViewModel.Factory(repository, selectedPetType)
    )

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var textFieldValue by rememberSaveable { mutableStateOf(searchQuery) }

    LaunchedEffect(textFieldValue) {
        viewModel.updateSearchQuery(textFieldValue)
    }

    LaunchedEffect(selectedPetType) {
        viewModel.updatePetType(selectedPetType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botões para escolher Gato ou Cachorro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedPetType == "cat",
                onClick = { selectedPetType = "cat" },
                label = { Text("🐱 Gatos", fontSize = 16.sp) }
            )
            FilterChip(
                selected = selectedPetType == "dog",
                onClick = { selectedPetType = "dog" },
                label = { Text("🐶 Cachorros", fontSize = 16.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Digite o nome da raça...", fontSize = 16.sp) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Buscando ${if (selectedPetType == "cat") "gatos" else "cachorros"}...", fontSize = 16.sp)
                    }
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌ ${uiState.errorMessage}", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Tentar novamente", fontSize = 14.sp)
                        }
                    }
                }
            }

            uiState.isEmptyResult && !uiState.isFirstLoad -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😢 Nenhuma raça encontrada para \"$searchQuery\"", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tente outro nome de raça", style = MaterialTheme.typography.bodySmall, fontSize = 14.sp)
                    }
                }
            }

            uiState.isFirstLoad -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍 Escolha Gatos ou Cachorros e digite uma raça", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedPetType == "cat") "Exemplos: Bengal, Siamese, Persian" else "Exemplos: Labrador, Beagle, Poodle",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            uiState.breeds.isNotEmpty() -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.breeds) { breed ->
                        BreedCard(breed = breed)
                    }
                }
            }
        }
    }
}

@Composable
fun BreedCard(breed: Breed) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Nome da raça em destaque
            Text(
                text = breed.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Imagem ocupando largura total
            AsyncImage(
                model = breed.imageUrl,
                contentDescription = "Imagem de ${breed.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Origem
            if (breed.origin.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Origem",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Origem: ${breed.origin}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Temperamento
            if (breed.temperament.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Temperamento",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Temperamento: ${breed.temperament}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp
                    )
                }
            }

            // Descrição
            if (breed.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = breed.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    Prova1_API_catdogTheme {
        SearchScreen()
    }
}