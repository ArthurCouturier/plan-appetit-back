package fr.planappetit.planappetitback.models.recipes.generation

data class RecipeFromOpenAI(
    val title: String,
    val ingredients: List<IngredientFromOpenAI>?,
    val steps: List<StepFromOpenAI>?,
    var coversNb: Int = 0,
    val costPrice: Number = 0,
    val sellPrice: Number = 0,
)
