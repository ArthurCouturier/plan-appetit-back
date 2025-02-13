package fr.planappetit.planappetitback.models.users

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @JsonIgnore
    var user: User? = null
) {
    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PremiumAdvantages

        if (remainingGeneratedRecipes != other.remainingGeneratedRecipes) return false
        if (uuid != other.uuid) return false
        if (user != other.user) return false

        return true
    }
}
