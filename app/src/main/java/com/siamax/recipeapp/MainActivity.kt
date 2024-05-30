package com.siamax.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.selection.SelectionContainer
import com.siamax.recipeapp.ui.theme.RecipeAppTheme
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeAppTheme {
                RecipeGeneratorScreen()
            }
        }
    }
}

@Composable
fun RecipeGeneratorScreen() {
    var ingredients by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(emptyList<String>()) }

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
                        onValueChange = { ingredients = it },
                        modifier = Modifier.weight(0.8f),
                        label = { Text("Enter ingredients") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            // Use coroutine scope to launch a coroutine
                            coroutineScope.launch {
                                // Call generateRecipe within the coroutine scope
                                recipes = generateRecipe(ingredients)
                            }
                        },
                        modifier = Modifier.weight(0.35f)
                    ) {
                        Text("Generate")
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display each recipe separately
                    recipes.forEach { recipe ->
                        SelectionContainer {
                            Text(recipe)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}


suspend fun generateRecipe(ingredient: String): List<String> {
    return withContext(Dispatchers.IO) {
        val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$ingredient")
        val connection = url.openConnection() as HttpURLConnection

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                val jsonResponse = JSONObject(response)
                val mealsArray = jsonResponse.getJSONArray("meals")
                val recipes = mutableListOf<String>()

                for (i in 0 until mealsArray.length()) {
                    val meal = mealsArray.getJSONObject(i)
                    val mealName = meal.getString("strMeal")
                    val instructions = meal.getString("strInstructions")
                    val ingredients = StringBuilder()
                    val measures = StringBuilder()

                    // Collecting ingredients and measures
                    for (j in 1..20) {
                        val ingredientKey = "strIngredient$j"
                        val measureKey = "strMeasure$j"
                        val ingredient = meal.getString(ingredientKey)
                        val measure = meal.getString(measureKey)

                        if (ingredient.isNotEmpty()) {
                            ingredients.append("$ingredient: $measure\n")
                        }
                    }

                    // Formatting the recipe
                    val recipe = "$mealName\nIngredients:\n$ingredients\nInstructions:\n$instructions\n"
                    recipes.add(recipe)
                }

                return@withContext recipes
            } else {
                return@withContext listOf("Error: $responseCode ${connection.responseMessage}")
            }
        } catch (e: Exception) {
            return@withContext listOf("Error: ${e.message}")
        } finally {
            connection.disconnect()
        }
    }
}






@Preview(showBackground = true)
@Composable
fun RecipeGeneratorScreenPreview() {
    RecipeAppTheme {
        RecipeGeneratorScreen()
    }
}
