package fr.planappetit.planappetitback.controllers

import fr.planappetit.planappetitback.enums.SeasonEnum
import fr.planappetit.planappetitback.models.recipes.generation.RecipeGenerationParameters
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.services.RecipeGenerationService
import fr.planappetit.planappetitback.services.RecipeService
import fr.planappetit.planappetitback.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/recipes")
class RecipeController(
    val userService: UserService,
    val recipeService: RecipeService,
    val recipeGenerationService: RecipeGenerationService
) {
    @GetMapping("/test")
    fun test(): Recipe? {
        val recipeFromOpenAI = recipeGenerationService.registerRecipe(RecipeGenerationParameters(
            localisation = "Toulouse",
            seasons = listOf(SeasonEnum.WINTER),
            ingredient = "",
            book = false,
            vegan = false,
            allergens = "",
            buyPrice = 15,
            sellingPrice = 25
        ))
        val user = userService.findAll()[0]
        val recipe = Recipe(recipeFromOpenAI =  recipeFromOpenAI, user = user)
        recipeService.registerNewRecipe(recipe)
        user.recipes.add(recipe)
        userService.saveUser(user)
        return recipe
    }
}