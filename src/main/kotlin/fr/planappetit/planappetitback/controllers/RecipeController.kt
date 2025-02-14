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

    @GetMapping("/all")
    fun allRecipes(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<List<Recipe>> {
        try {
            val user: User? = userService.getUserSecurely(email, token)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            val recipes: MutableList<Recipe> = user.recipes
            return ResponseEntity(recipes, HttpStatus.OK)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/generate")
    fun generateNewRecipeWithOpenAI(
        @RequestBody generationParameters: RecipeGenerationParameters,
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Recipe?> {
        try {
            val user: User? = userService.getUserSecurely(email, token)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            val recipe: Recipe? = recipeService.generateNewRecipeWithOpenAI(
                user = user,
                params = generationParameters
            )
            if (recipe == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
            }
            userService.saveUser(user)
            return ResponseEntity.status(HttpStatus.OK).body(recipe)

        } catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/create")
    fun createRecipe(
        @RequestHeader("Email") email: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Recipe> {
        try {
            val user: User? = userService.getUserSecurely(email, token)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            val recipe: Recipe = recipeService.createEmptyRecipe(user)
            userService.saveUser(user)
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
    ) : ResponseEntity<Recipe> {
        try {
            val user: User? = userService.getUserSecurely(email, token)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            recipeService.importNewRecipe(recipe, user)
            userService.saveUser(user)
            return ResponseEntity.status(HttpStatus.OK).body(recipe)

        }  catch (e: UnCheckedIdentityException) {
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
            val user: User? = userService.getUserSecurely(email, token)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            recipeService.deleteRecipeByUuid(id)
            userService.saveUser(user)
            return ResponseEntity.status(HttpStatus.OK).build()

        }  catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}