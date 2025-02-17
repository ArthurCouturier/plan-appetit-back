package fr.planappetit.planappetitback.controllers

import fr.planappetit.planappetitback.exceptions.UnCheckedIdentityException
import fr.planappetit.planappetitback.models.recipes.generation.RecipeGenerationParameters
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.services.RecipeService
import fr.planappetit.planappetitback.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/recipes")
class RecipeController(
    private val userService: UserService,
    private val recipeService: RecipeService,
) {

    @PostMapping("/create")
    fun createNewEmptyRecipe(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Recipe> {
        try {
            val user: User = userService.authenticateAndSyncUser(email, token)

            val recipe: Recipe = recipeService.createEmptyRecipe(user)
            userService.saveOrUpdateUser(user)
            return ResponseEntity.status(HttpStatus.OK).body(recipe)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

    }

    @PostMapping("/generate")
    fun createNewRecipeWithOpenAI(
        @RequestBody generationParameters: RecipeGenerationParameters,
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Recipe?> {
        try {
            val user: User = userService.authenticateAndSyncUser(email, token)

            val recipe: Recipe? = recipeService.generateNewRecipeWithOpenAI(
                user = user,
                params = generationParameters
            )
            if (recipe == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }
            user.premiumAdvantages!!.remainingGeneratedRecipes--
            recipe.user = user
            userService.saveOrUpdateUser(user)
            return ResponseEntity.status(HttpStatus.OK).body(recipe)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/import")
    fun importRecipe(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
        @RequestBody recipe: Recipe,
    ): ResponseEntity<Recipe> {
        try {
            val user: User = userService.authenticateAndSyncUser(email, token)

            recipeService.importNewRecipe(recipe, user)
            return ResponseEntity.status(HttpStatus.OK).body(recipe)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/all")
    fun readAllRecipesByUser(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<List<Recipe>> {
        try {
            val user: User = userService.authenticateAndSyncUser(email, token)

            val recipes: MutableList<Recipe> = user.recipes
            return ResponseEntity(recipes, HttpStatus.OK)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PatchMapping("/update")
    fun updateRecipe(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
        @RequestBody recipe: Recipe,
    ): ResponseEntity<Recipe> {
        try {
            val user = userService.authenticateAndSyncUser(email, token)

            recipe.user = user
            recipe.ingredients.forEach { ingredient ->
                ingredient.quantity = ingredient.quantity
                ingredient.recipe = recipe
            }
            recipe.steps.forEach { step ->
                step.recipe = recipe
            }
            return ResponseEntity.status(HttpStatus.OK).body(recipeService.saveRecipe(recipe))

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteRecipe(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") id: String
    ): ResponseEntity<Unit> {
        try {
            val user: User = userService.authenticateAndSyncUser(email, token)

            recipeService.deleteRecipeByUuid(id)
            userService.saveOrUpdateUser(user)
            return ResponseEntity.status(HttpStatus.OK).build()

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}