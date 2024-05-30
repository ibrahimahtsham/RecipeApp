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
                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            // Use coroutine scope to launch a coroutine
                            coroutineScope.launch {
                                // Call generateRecipe within the coroutine scope
                                recipe = generateRecipe(ingredients)
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
                    SelectionContainer {
                        Text(recipe)
                    }
                }
            }
        }
    )
}

suspend fun generateRecipe(ingredients: String): String {
    return withContext(Dispatchers.IO) {
        val apiKey = "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"
        val url = URL("https://api.openai.com/v1/completions")
        val connection = url.openConnection() as HttpURLConnection

        try {
            // Set request properties
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.doOutput = true

            // Create request body
            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")  // Use the gpt-3.5-turbo model
                put("prompt", "Generate a recipe using ingredients: $ingredients")
                put("max_tokens", 500)
                put("temperature", 0.7)  // Adjust the temperature if needed
            }

            // Write request body to output stream
            val outputStream = connection.outputStream
            outputStream.use { it.write(requestBody.toString().toByteArray()) }

            // Read response
            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorStream = connection.errorStream
                val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                "Error: $responseCode ${connection.responseMessage} $errorResponse"
            }

            // Parse response
            return@withContext try {
                val jsonResponse = JSONObject(response)
                jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text")
            } catch (e: Exception) {
                "Error: ${e.message} Response: $response"
            }

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
