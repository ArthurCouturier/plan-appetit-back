package fr.planappetit.planappetitback.models.recipes.usual

import fr.planappetit.planappetitback.enums.UnitEnum
import fr.planappetit.planappetitback.models.recipes.generation.QuantityFromOpenAI
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Quantity(
    @Id
    @GeneratedValue(generator = "uuid2")
    var uuid: UUID? = null,
    var value: Number = 0,
    var unit: UnitEnum? = null
) {
    constructor() : this(
        uuid = null,
        value = 0,
        unit = null
    )

    constructor(quantityFromOpenAI: QuantityFromOpenAI) : this(
        uuid = null,
        value = quantityFromOpenAI.value,
        unit = quantityFromOpenAI.unit
    )
}
