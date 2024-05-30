package com.siamax.recipeapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Recipe(val id: String, val name: String, val thumbnail: String)

suspend fun generateRecipe(ingredient: String): List<Recipe> {
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
                    return@withContext listOf(Recipe("0", "No recipes found for the ingredient: $ingredient", ""))
                }

                val mealsArray = jsonResponse.getJSONArray("meals")
                val recipes = mutableListOf<Recipe>()

                for (i in 0 until mealsArray.length()) {
                    val meal = mealsArray.getJSONObject(i)
                    val mealId = meal.getString("idMeal")
                    val mealName = meal.getString("strMeal")
                    val mealThumbnail = meal.getString("strMealThumb")
                    recipes.add(Recipe(mealId, mealName, mealThumbnail))
                }

                return@withContext recipes
            } else {
                return@withContext listOf(Recipe("0", "Error: $responseCode ${connection.responseMessage}", ""))
            }
        } catch (e: Exception) {
            return@withContext listOf(Recipe("0", "Something went wrong: ${e.message}", ""))
        } finally {
            connection.disconnect()
        }
    }
}

