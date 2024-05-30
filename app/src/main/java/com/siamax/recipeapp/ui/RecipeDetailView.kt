// RecipeDetailView.kt

package com.siamax.recipeapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.siamax.recipeapp.network.getRecipeDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamax.recipeapp.network.RecipeDetail
import androidx.compose.runtime.setValue

@Composable
fun RecipeDetailView(recipeId: String) {
    var recipe: RecipeDetail? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch(Dispatchers.IO) {
            recipe = getRecipeDetail(recipeId)
        }
    }

    Column {
        recipe?.let {
            Text(
                text = it.name,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = it.instructions ?: "",
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
