package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.recipes.usual.Quantity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuantityRepository : JpaRepository<Quantity, String> {
}