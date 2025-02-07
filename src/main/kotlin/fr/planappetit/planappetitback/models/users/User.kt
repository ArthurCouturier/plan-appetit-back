package fr.planappetit.planappetitback.models.users

import fr.planappetit.planappetitback.enums.UserRole
import jakarta.persistence.Entity
import jakarta.persistence.Id
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
    var token: String? = null,
    var provider: String = "",
    var role: UserRole = UserRole.MEMBER,
    var createdAt: Date = Date(),
    var lastLogin: Date? = null,
    var profilePhoto: String? = null
)