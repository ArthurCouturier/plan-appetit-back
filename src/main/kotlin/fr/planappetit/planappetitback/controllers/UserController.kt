package fr.planappetit.planappetitback.controllers

import fr.planappetit.planappetitback.exceptions.UnCheckedIdentityException
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.services.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Date

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @Value("app.admin.secret")
    private val appAdminSecret: String? = null

    @PostMapping("/register")
    fun register(
        @RequestHeader("Authorization") token: String,
        @RequestBody user: User
    ): ResponseEntity<User?> {
        val userFound = userService.authenticateAndSyncUser(user.email, token)
        val savedUser = userService.saveOrUpdateUser(userFound)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<User?> {
        val user = userService.getUserByUid(id)
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }

    @GetMapping("/all")
    fun getAllUsers(@RequestHeader("Authorization") authorizationHeader: String): ResponseEntity<List<User>> {
        if (!authorizationHeader.equals(appAdminSecret)) {
            throw (RuntimeException("Unauthorized")) as Throwable;
        }
        val users = userService.getAllUsers()
        return ResponseEntity.status(HttpStatus.OK).body(users)
    }

    @GetMapping("/connect")
    fun connect(
        @RequestHeader("Authorization") token: String,
        @RequestHeader("Email") email: String
    ): ResponseEntity<User?> {
        try {
            val user: User? = userService.authenticateAndSyncUser(email, token)
            if  (user != null) {
                user.lastLogin = Date.from(Instant.now())
                userService.saveOrUpdateUser(user)
                return ResponseEntity.status(HttpStatus.OK).body(user)
            }
        }  catch (e: UnCheckedIdentityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
    }

    @GetMapping("/{id}/recipes")
    fun getRecipes(@PathVariable id: String): ResponseEntity<List<Recipe>> {
        val user = userService.getUserByUid(id)
        if (user != null) {
            val recipes = userService.getRecipesForUser(user)
            return ResponseEntity.status(HttpStatus.OK).body(recipes)
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }
}
