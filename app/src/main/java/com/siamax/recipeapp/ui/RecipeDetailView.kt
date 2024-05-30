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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment

@Composable
fun RecipeDetailView(recipeId: String) {
    var recipe: RecipeDetail? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch(Dispatchers.IO) {
            recipe = getRecipeDetail(recipeId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp)
            ) {
                recipe?.let {
                    Text(
                        text = it.name,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally)
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
    )
}
