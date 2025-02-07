package fr.planappetit.planappetitback.models.recipes

import fr.planappetit.planappetitback.enums.UnitEnum
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Quantity(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    var value: Number = 0,
    var unit: UnitEnum = UnitEnum.NONE
)
