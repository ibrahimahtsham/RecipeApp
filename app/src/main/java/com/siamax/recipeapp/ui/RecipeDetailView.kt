package com.siamax.recipeapp.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamax.recipeapp.network.RecipeDetail
import com.siamax.recipeapp.network.getRecipeDetail
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailView(recipeId: String) {
    var recipe: RecipeDetail? by remember { mutableStateOf(null) }
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch(Dispatchers.IO) {
            recipe = getRecipeDetail(recipeId)
            recipe?.imageUrl?.let {
                val url = URL(it)
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()
                val input = connection.getInputStream()
                val bmp = BitmapFactory.decodeStream(input)
                bitmap = bmp.asImageBitmap()
            }
        }
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
                        bitmap?.let { bmp ->
                            Image(
                                    bitmap = bmp,
                                    contentDescription = "Recipe Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                            )
                        }

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
