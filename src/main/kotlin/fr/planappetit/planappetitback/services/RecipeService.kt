package fr.planappetit.planappetitback.services

import fr.planappetit.planappetitback.models.recipes.Recipe
import fr.planappetit.planappetitback.repositories.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeService(private val recipeRepository: RecipeRepository) {

    fun findRecipeByUuid(uuid: String): Recipe? {
        return recipeRepository.findById(uuid).orElse(null)
    }

    fun findAll(): List<Recipe> {
        return recipeRepository.findAll()
    }

    fun saveRecipe(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    fun deleteRecipeByUuid(uuid: String) {
        recipeRepository.deleteById(uuid)
    }
}