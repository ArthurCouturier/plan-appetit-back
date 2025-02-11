package fr.planappetit.planappetitback.models.users

import fr.planappetit.planappetitback.enums.UserRole
import fr.planappetit.planappetitback.models.recipes.Recipe
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "users")
data class User(
    // Firebase Generated ID
    @Id
    val uid: String = "",
    var email: String = "",
    var displayName: String = "",
    @Column(length = 2048)
    var token: String? = null,
    var provider: String = "",
    var role: UserRole = UserRole.MEMBER,
    var createdAt: Date = Date(),
    var lastLogin: Date? = null,
    var profilePhoto: String? = null,
    @OneToMany(mappedBy = "user")
    var recipes: MutableList<Recipe> = mutableListOf(),
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var premiumAdvantages: PremiumAdvantages? = null,
) {

    @PrePersist
    fun prePersist() {
        if (premiumAdvantages == null) {
            premiumAdvantages = PremiumAdvantages(user = this)
        }
    }
}
