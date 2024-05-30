package com.siamax.recipeapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.siamax.recipeapp.network.Recipe
import com.siamax.recipeapp.network.generateRecipe
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun RecipeGeneratorScreen() {
    var selectedRecipeId by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = selectedRecipeId != null) {
        selectedRecipeId = null // Reset selectedRecipeId when back button is pressed
    }

    if (selectedRecipeId != null) {
        RecipeDetailView(recipeId = selectedRecipeId!!)
    } else {
    var ingredients by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(emptyList<Recipe>()) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = ingredients,
                        onValueChange = {
                            ingredients = it
                            isError = ingredients.isEmpty()
                        },
                        modifier = Modifier.weight(0.8f),
                        label = { Text("Enter ingredients") },
                        isError = isError,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                recipes = generateRecipes(ingredients)
                                isLoading = false
                            }
                        },
                        modifier = Modifier.weight(0.35f),
                        enabled = !isError && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Generate")
                        }
                    }
                }
                if (isError) {
                    Text(
                        text = "Input cannot be empty",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (recipes.isEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            append("Enter ingredients to generate recipe\n")
                            withStyle(style = SpanStyle(fontSize = 12.sp)) {
                                append("E.g. chicken, rice, carrot")
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    ) }

                    recipes.forEach { recipe ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                var bitmap by remember { mutableStateOf<Bitmap?>(null) }
                                LaunchedEffect(recipe.thumbnail) {
                                    bitmap = loadImage(recipe.thumbnail)
                                }
                                bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${recipe.name}",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        // Set selectedRecipeId to the desired recipeId when button is clicked
                                        selectedRecipeId = recipe.id // Replace "recipeId" with the actual recipe ID
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth() // This modifier makes the button take the full width of its parent

                                ) {
                                    Text("View Recipe")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
}


suspend fun generateRecipes(ingredients: String): List<Recipe> {
    val ingredientList = ingredients.split(",").map { it.trim() }
    val allRecipes = mutableListOf<Recipe>()

    for (ingredient in ingredientList) {
        val recipes = generateRecipe(ingredient)
        allRecipes.addAll(recipes)
    }

    return allRecipes
}

suspend fun loadImage(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeGeneratorScreenPreview() {
    RecipeGeneratorScreen()
}
