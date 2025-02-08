package fr.planappetit.planappetitback.models.users

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.util.UUID

@Entity
data class PremiumAdvantages(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    var remainingGeneratedRecipes: Int = 3,
    @OneToOne()
    @JoinColumn(name = "user_uid", nullable = false)
    var user: User? = null
)
