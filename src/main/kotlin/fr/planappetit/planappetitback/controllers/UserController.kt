package fr.planappetit.planappetitback.controllers

import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.services.FirebaseService
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

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @Value("app.admin.secret")
    private val appAdminSecret: String? = null

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<User?> {
        val savedUser = userService.saveUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<User?> {
        val user = userService.findUserByUid(id)
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }

    @GetMapping("/all")
    fun getAllUsers(@RequestHeader("Authorization") authorizationHeader: String): ResponseEntity<List<User>> {
        if (!authorizationHeader.equals(appAdminSecret)) {
            throw (RuntimeException("Unauthorized")) as Throwable;
        }
        val users = userService.findAll()
        return ResponseEntity.status(HttpStatus.OK).body(users)
    }

    @GetMapping("/checkProfile")
    fun checkProfile(@RequestHeader("Authorization") token: String): ResponseEntity<User?> {
        val auth: Boolean = userService.verifyUser("arthur.couturier.2000@gmail.com", token.removePrefix("Bearer "))
        if (auth) {
            return ResponseEntity.status(HttpStatus.OK).body(null)
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}
