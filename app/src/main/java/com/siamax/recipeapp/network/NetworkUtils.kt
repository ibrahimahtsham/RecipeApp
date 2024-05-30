package com.siamax.recipeapp.network

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class RecipeDetail(
        val id: String,
        val name: String,
        val instructions: String?,
        val category: String?,
        val area: String?,
        val tags: String?,
        val imageUrl: String?,
        val youtubeUrl: String?,
        val ingredients: List<String>,
        val measures: List<String>,
        val drinkAlternate: String?,
        val source: String?,
        val imageSource: String?,
        val creativeCommonsConfirmed: String?,
        val dateModified: String?
)

suspend fun getRecipeDetail(recipeId: String): RecipeDetail? {
    return withContext(Dispatchers.IO) {
        val url = URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$recipeId")
        val connection = url.openConnection() as HttpURLConnection

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                val jsonResponse = JSONObject(response)
                val mealsArray = jsonResponse.getJSONArray("meals")
                if (mealsArray.length() > 0) {
                    val meal = mealsArray.getJSONObject(0)
                    val mealId = meal.getString("idMeal")
                    val mealName = meal.getString("strMeal")
                    val mealInstructions = meal.getString("strInstructions")
                    val mealCategory = meal.getString("strCategory")
                    val mealArea = meal.getString("strArea")
                    val mealTags = meal.getString("strTags")
                    val mealImageUrl = meal.getString("strMealThumb")
                    val mealYoutubeUrl = meal.getString("strYoutube")
                    val mealDrinkAlternate = meal.optString("strDrinkAlternate", null)
                    val mealSource = meal.optString("strSource", null)
                    val mealImageSource = meal.optString("strImageSource", null)
                    val mealCreativeCommonsConfirmed =
                            meal.optString("strCreativeCommonsConfirmed", null)
                    val mealDateModified = meal.optString("dateModified", null)
                    val ingredients =
                            (1..20)
                                    .mapNotNull { i -> meal.optString("strIngredient$i", null) }
                                    .filter { it.isNotBlank() }
                    val measures =
                            (1..20)
                                    .mapNotNull { i -> meal.optString("strMeasure$i", null) }
                                    .filter { it.isNotBlank() }
                    RecipeDetail(
                            mealId,
                            mealName,
                            mealInstructions,
                            mealCategory,
                            mealArea,
                            mealTags,
                            mealImageUrl,
                            mealYoutubeUrl,
                            ingredients,
                            measures,
                            mealDrinkAlternate,
                            mealSource,
                            mealImageSource,
                            mealCreativeCommonsConfirmed,
                            mealDateModified
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }
}

data class Recipe(val id: String, val name: String, val thumbnail: String)

suspend fun generateRecipe(ingredient: String): List<Recipe> {
    return withContext(Dispatchers.IO) {
        val url = URL("https://www.themealdb.com/api/json/v1/1/filter.php?i=$ingredient")
        val connection = url.openConnection() as HttpURLConnection

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                val jsonResponse = JSONObject(response)
                if (jsonResponse.isNull("meals")) {
                    return@withContext listOf(
                            Recipe("0", "No recipes found for the ingredient: $ingredient", "")
                    )
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
                return@withContext listOf(
                        Recipe("0", "Error: $responseCode ${connection.responseMessage}", "")
                )
            }
        } catch (e: Exception) {
            return@withContext listOf(Recipe("0", "Something went wrong: ${e.message}", ""))
        } finally {
            connection.disconnect()
        }
    }
}
