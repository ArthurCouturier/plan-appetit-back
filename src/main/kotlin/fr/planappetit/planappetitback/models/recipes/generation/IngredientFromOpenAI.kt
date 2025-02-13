package fr.planappetit.planappetitback.models.recipes.generation

import fr.planappetit.planappetitback.enums.IngredientCategoryEnum
import fr.planappetit.planappetitback.enums.SeasonEnum

data class IngredientFromOpenAI(
    var name: String = "",
    var category: IngredientCategoryEnum = IngredientCategoryEnum.OTHER,
    var season: MutableList<SeasonEnum> = mutableListOf(SeasonEnum.ALL),
    var quantity: QuantityFromOpenAI? = null,
)
