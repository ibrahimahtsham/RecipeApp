package com.siamax.recipeapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamax.recipeapp.network.RecipeDetail
import com.siamax.recipeapp.network.getRecipeDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailView(recipeId: String) {
    var recipe: RecipeDetail? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch(Dispatchers.IO) { recipe = getRecipeDetail(recipeId) }
    }

    Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(padding)
                                        .padding(16.dp)
                ) {
                    recipe?.let {
                        Text(
                                text = it.name,
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                modifier =
                                        Modifier.padding(vertical = 8.dp)
                                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = it.instructions ?: "",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Category: ${it.category}",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Area: ${it.area}",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Tags: ${it.tags}",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        it.ingredients.zip(it.measures).forEach { (ingredient, measure) ->
                            Text(
                                    text = "$ingredient: $measure",
                                    textAlign = TextAlign.Start,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
    )
}
