package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, String> {

}
