package fr.planappetit.planappetitback.models.recipes

import fr.planappetit.planappetitback.enums.IngredientCategoryEnum
import fr.planappetit.planappetitback.enums.SeasonEnum
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import java.util.UUID

@Entity
data class Ingredient(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "",
    var category: IngredientCategoryEnum = IngredientCategoryEnum.OTHER,
    var season: List<SeasonEnum> = listOf<SeasonEnum>(SeasonEnum.ALL),
    @OneToOne
    var quantity: Quantity = Quantity(),
    @ManyToOne(cascade = [CascadeType.ALL])
    val recipe: Recipe? = null
)
