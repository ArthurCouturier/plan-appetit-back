package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RecipeRepository : JpaRepository<Recipe, String> {
    fun deleteByUuid(uuid: UUID)
    fun findByUuid(uUID: java.util.UUID): Recipe?
}
