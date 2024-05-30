package com.siamax.recipeapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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
                if (jsonResponse.isNull("meals")) {
                    return@withContext listOf("No recipes found for the ingredient: $ingredient")
                }

                val mealsArray = jsonResponse.getJSONArray("meals")
                val recipes = mutableListOf<String>()

                for (i in 0 until mealsArray.length()) {
                    val meal = mealsArray.getJSONObject(i)
                    val mealName = meal.getString("strMeal")
                    recipes.add(mealName)
                }

                return@withContext recipes
            } else {
                return@withContext listOf("Error: $responseCode ${connection.responseMessage}")
            }
        } catch (e: Exception) {
            return@withContext listOf("Something went wrong: ${e.message}")
        } finally {
            connection.disconnect()
        }
    }
}
