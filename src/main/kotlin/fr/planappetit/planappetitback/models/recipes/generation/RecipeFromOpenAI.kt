package fr.planappetit.planappetitback.models.recipes.generation

data class RecipeFromOpenAI(
    val title: String,
    val ingredients: List<IngredientFromOpenAI>?,
    val steps: List<StepFromOpenAI>?,
    var coversNb: Int = 0,
    val costPrice: Number,
    val buyPrice: Number,
    val sellPrice: Number,
)
