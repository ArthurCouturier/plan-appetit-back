package fr.planappetit.planappetitback.services

import fr.planappetit.planappetitback.enums.UnitEnum
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.repositories.IngredientRepository
import fr.planappetit.planappetitback.repositories.QuantityRepository
import fr.planappetit.planappetitback.repositories.RecipeRepository
import fr.planappetit.planappetitback.repositories.StepRepository
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val quantityRepository: QuantityRepository,
    private val stepRepository: StepRepository
) {

    fun findRecipeByUuid(uuid: String): Recipe? {
        return recipeRepository.findById(uuid).orElse(null)
    }

    fun findAll(): List<Recipe> {
        return recipeRepository.findAll()
    }

    fun registerNewRecipe(recipe: Recipe): Recipe {
        recipe.ingredients.forEach { ingredient ->
            if (ingredient.quantity != null) {
                if (!UnitEnum.entries.contains(ingredient.quantity!!.unit)) {
                    ingredient.quantity!!.unit = UnitEnum.NONE
                }
                quantityRepository.save(ingredient.quantity!!)
            }
            ingredientRepository.save(ingredient)
        }
        recipe.steps.forEach { step ->
            stepRepository.save(step)
        }
        return recipeRepository.save(recipe)
    }

    fun deleteRecipeByUuid(uuid: String) {
        recipeRepository.deleteById(uuid)
    }
}