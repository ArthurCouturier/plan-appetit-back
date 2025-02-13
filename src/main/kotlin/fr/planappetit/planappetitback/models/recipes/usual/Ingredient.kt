package fr.planappetit.planappetitback.models.recipes.usual

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.planappetit.planappetitback.enums.IngredientCategoryEnum
import fr.planappetit.planappetitback.enums.SeasonEnum
import fr.planappetit.planappetitback.models.recipes.generation.IngredientFromOpenAI
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import java.util.UUID

@Entity
data class Ingredient(
    @Id
    @GeneratedValue(generator = "uuid2")
    var uuid: UUID? = null,
    var name: String = "",
    var category: IngredientCategoryEnum = IngredientCategoryEnum.OTHER,
    var season: MutableList<SeasonEnum> = mutableListOf(SeasonEnum.ALL),
    @OneToOne
    var quantity: Quantity? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JsonIgnore
    var recipe: Recipe? = null
) {
    constructor() : this(
        uuid = null,
        name = "",
        category = IngredientCategoryEnum.OTHER,
        season = mutableListOf(),
        quantity = null
    )

    constructor(ingredientFromOpenAI: IngredientFromOpenAI) : this(
        uuid = null,
        name = ingredientFromOpenAI.name,
        category = ingredientFromOpenAI.category,
        season = ingredientFromOpenAI.season,
        quantity = if (ingredientFromOpenAI.quantity != null) Quantity(ingredientFromOpenAI.quantity!!) else null,
    )
}
