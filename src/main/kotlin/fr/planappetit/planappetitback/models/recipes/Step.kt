package fr.planappetit.planappetitback.models.recipes

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class Step (
    @Id
    val uuid: UUID = UUID.randomUUID(),
    var key: Number,
    var value: Number,
    @ManyToOne(cascade = [CascadeType.ALL])
    val recipe: Recipe? = null
)
