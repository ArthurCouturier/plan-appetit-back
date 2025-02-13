package fr.planappetit.planappetitback.models.recipes.generation

import fr.planappetit.planappetitback.enums.UnitEnum

data class QuantityFromOpenAI(
    var value: Number = 0,
    var unit: UnitEnum? = null
)
