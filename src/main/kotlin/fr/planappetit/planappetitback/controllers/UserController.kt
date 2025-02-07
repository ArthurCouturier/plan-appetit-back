package fr.planappetit.planappetitback.controllers

import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<User?> {
        val savedUser = userService.saveUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable uid: String): ResponseEntity<User?> {
        val user = userService.findUserByUid(uid)
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }

    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.findAll()
        return ResponseEntity.status(HttpStatus.OK).body(users)
    }
}
