package fr.planappetit.planappetitback.services

import fr.planappetit.planappetitback.models.recipes.generation.RecipeGenerationParameters
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.repositories.IngredientRepository
import fr.planappetit.planappetitback.repositories.QuantityRepository
import fr.planappetit.planappetitback.repositories.RecipeRepository
import fr.planappetit.planappetitback.repositories.StepRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val quantityRepository: QuantityRepository,
    private val stepRepository: StepRepository,
    private val recipeGenerationService: RecipeGenerationService
) {

    fun createEmptyRecipe(user: User): Recipe {
        val recipe = Recipe(user = user)
        this.saveRecipe(recipe)
        user.recipes.add(recipe)
        return recipe
    }

    fun saveRecipe(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    fun generateNewRecipeWithOpenAI(
        user: User,
        params: RecipeGenerationParameters
    ): Recipe? {
        val recipeFromOpenAI = recipeGenerationService.registerRecipe(params)
        val recipe = Recipe(recipeFromOpenAI = recipeFromOpenAI, user = user)
        this.saveRecipe(recipe)
        user.recipes.add(recipe)
        return recipe
    }

    fun importNewRecipe(
        recipe: Recipe,
        user: User
    ): Recipe {
        recipe.uuid = null
        recipe.ingredients.forEach { ingredient ->
            ingredient.quantity!!.uuid = null
            ingredient.uuid = null
            ingredient.recipe = null
        }
        recipe.steps.forEach { step -> step.uuid = null }
        recipe.user = user
        return this.saveRecipe(recipe)
    }

    fun deleteRecipeByUuid(uuid: String) {
        val recipe: Recipe? = recipeRepository.findByUuid(UUID.fromString(uuid))
        if (recipe != null) {
            recipe.ingredients.forEach { ingredient ->
                quantityRepository.delete(ingredient.quantity!!)
                ingredientRepository.delete(ingredient)
            }
            recipe.steps.forEach { step ->
                stepRepository.delete(step)
            }
            recipeRepository.delete(recipe)
        }
    }
}