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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Favorite

// Como Icons.Default.Pets pode exigir a biblioteca 'extended', vamos usar um ícone padrão do Compose
// para garantir que não quebre o seu projeto agora.
val PetIcon = Icons.Default.Favorite 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var selectedPetType by remember { mutableStateOf("dog") }

    val repository = remember {
        BreedRepository(
            RetrofitInstance.catApiService,
            RetrofitInstance.dogApiService
        )
    }

    val viewModel: BreedsViewModel = viewModel(
        factory = BreedsViewModel.Factory(repository, selectedPetType)
    )

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var textFieldValue by rememberSaveable { mutableStateOf(searchQuery) }

    // Cores do layout (inspirado na imagem)
    val backgroundColor = Color(0xFFFFFBF0)
    val brandOrange = Color(0xFFE65100)
    val dogGreen = Color(0xFF00897B)
    val catGray = Color(0xFF455A64)

    LaunchedEffect(textFieldValue) {
        viewModel.updateSearchQuery(textFieldValue)
    }

    LaunchedEffect(selectedPetType) {
        viewModel.updatePetType(selectedPetType)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = PetIcon,
                    contentDescription = null,
                    tint = brandOrange,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BuscaPet",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandOrange
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = brandOrange,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de busca arredondada
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
                placeholder = { 
                    Text(
                        "Encontre seu novo melhor amigo", 
                        color = Color.LightGray,
                        fontSize = 16.sp
                    ) 
                },
                leadingIcon = { 
                    Icon(Icons.Default.Search, contentDescription = null, tint = brandOrange) 
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = brandOrange
                ),
                shape = RoundedCornerShape(28.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botões de Filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PetFilterButton(
                    text = "Cachorros",
                    isSelected = selectedPetType == "dog",
                    activeColor = dogGreen,
                    onClick = { selectedPetType = "dog" },
                    modifier = Modifier.weight(1f)
                )
                PetFilterButton(
                    text = "Gatos",
                    isSelected = selectedPetType == "cat",
                    activeColor = catGray,
                    onClick = { selectedPetType = "cat" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conteúdo Principal
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = brandOrange
                        )
                    }

                    uiState.errorMessage != null -> {
                        ErrorMessage(uiState.errorMessage!!) { viewModel.clearError() }
                    }

                    uiState.isFirstLoad -> {
                        Column {
                            Text(
                                "Sugestões para você",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.randomPhotos) { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    uiState.breeds.isNotEmpty() -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(uiState.breeds) { breed ->
                                BreedCard(breed = breed)
                            }
                        }
                    }

                    uiState.isEmptyResult -> {
                        Text(
                            "Nenhum pet encontrado.",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetFilterButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) activeColor.copy(alpha = 0.1f) else Color.White
    val contentColor = if (isSelected) activeColor else Color.DarkGray

    Surface(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .clickable { onClick() },
        color = backgroundColor,
        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = PetIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("❌ $message", color = Color.Red)
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))) {
            Text("Tentar novamente")
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
                    .height(180.dp)
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
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