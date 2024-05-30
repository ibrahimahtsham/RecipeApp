import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RecipeDetailView(recipeId: String) {
    // Fetch recipe details using the recipeId and display them

    Text(
        text = recipeId,
    )
}