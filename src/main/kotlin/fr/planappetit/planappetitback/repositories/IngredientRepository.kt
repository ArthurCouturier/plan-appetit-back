package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.recipes.usual.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, String> {
}