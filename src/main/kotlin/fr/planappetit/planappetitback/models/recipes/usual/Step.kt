package fr.planappetit.planappetitback.models.recipes.usual

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.planappetit.planappetitback.models.recipes.generation.StepFromOpenAI
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class Step (
    @Id
    @GeneratedValue(generator = "uuid2")
    var uuid: UUID? = null,
    var key: Number,
    var value: String,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JsonIgnore
    var recipe: Recipe? = null
) {
    constructor() : this(
        uuid = null,
        key = 0,
        value = "",
    )

    constructor(stepFromOpenAI: StepFromOpenAI) : this(
        uuid = null,
        key = stepFromOpenAI.key,
        value = stepFromOpenAI.value
    )
}
