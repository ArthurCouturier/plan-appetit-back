package fr.planappetit.planappetitback.models.recipes.generation

import fr.planappetit.planappetitback.enums.SeasonEnum

data class RecipeGenerationParameters(
    val localisation: String,
    val seasons: List<SeasonEnum>,
    val ingredients: String,
    val book: Boolean,
    val vegan: Boolean,
    val allergens: String,
    val buyPrice: Number,
    val sellingPrice: Number,
)