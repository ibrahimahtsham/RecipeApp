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
import com.siamax.recipeapp.ui.theme.RecipeAppTheme

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
    var recipe by remember { mutableStateOf("Generated recipe will appear here.") }

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
                    Button(
                        onClick = {
                            // Call your function to generate a recipe here
                            recipe = generateRecipe(ingredients)
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
                    Text(recipe)
                }
            }
        }
    )
}

fun generateRecipe(ingredients: String): String {
    // This is a stub. Replace with actual OpenAI API call.
    return "Generated recipe using ingredients: $ingredients"
}

@Preview(showBackground = true)
@Composable
fun RecipeGeneratorScreenPreview() {
    RecipeAppTheme {
        RecipeGeneratorScreen()
    }
}