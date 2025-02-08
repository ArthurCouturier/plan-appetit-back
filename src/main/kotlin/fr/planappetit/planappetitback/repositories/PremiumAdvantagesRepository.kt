package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.users.PremiumAdvantages
import fr.planappetit.planappetitback.models.users.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PremiumAdvantagesRepository : JpaRepository<PremiumAdvantages, UUID> {
    fun findByUser(user: User): PremiumAdvantages?
}