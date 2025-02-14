package fr.planappetit.planappetitback.services

import com.google.firebase.auth.FirebaseToken
import fr.planappetit.planappetitback.enums.UserRole
import fr.planappetit.planappetitback.exceptions.UnCheckedIdentityException
import fr.planappetit.planappetitback.models.recipes.usual.Recipe
import fr.planappetit.planappetitback.models.users.PremiumAdvantages
import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.repositories.PremiumAdvantagesRepository
import fr.planappetit.planappetitback.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val premiumAdvantagesRepository: PremiumAdvantagesRepository,
    private val firebaseService: FirebaseService,
) {
    fun getUserSecurely(
        email: String,
        token: String
    ): User {
        val checked: Boolean = this.verifyUserOnFirebaseAndGetOrCreate(email, token.removePrefix("Bearer "))
        if (!checked) {
            throw UnCheckedIdentityException()
        }
        val user = this.findUserByEmail(email)
        if (user == null) {
            throw UnCheckedIdentityException()
        }
        this.updateLastLoginOfExistingUser(user)
        return user
    }

    fun findUserByUid(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
    }

    fun saveUser(user: User): User {
        val userFound = this.findUserByEmail(user.email)
        if (userFound != null) {
            user.displayName = userFound.displayName
            user.createdAt = userFound.createdAt
            user.lastLogin = Date.from(Instant.now())
            user.uid = userFound.uid
        }

        var advantages: PremiumAdvantages? = premiumAdvantagesRepository.findByUser(user)
        if (advantages == null) {
            advantages = PremiumAdvantages(user = user)
            if (user.role > UserRole.MEMBER) {
                advantages.remainingGeneratedRecipes = 1000
            }
            val userCreated: User = userRepository.save(user)
            premiumAdvantagesRepository.save(advantages)
            return userCreated
        }
        val userCreated: User = userRepository.save(user)
        return userCreated
    }

    fun deleteUserByUid(uid: String) {
        userRepository.deleteById(uid)
    }

    private fun updateLastLoginOfExistingUser(user: User): Boolean {
        user.lastLogin = Date.from(Instant.now())
        this.userRepository.save(user)
        return true
    }

    private fun verifyUserOnFirebaseAndGetOrCreate(
        email: String,
        token: String
    ): Boolean {
        val decodedToken: FirebaseToken? = firebaseService.getFirebaseTokenFromAuthToken(token)

        if (decodedToken == null) {
            return false
        }

        val firebaseEmail = decodedToken.email
        val user = this.findUserByEmail(firebaseEmail)
        if (user == null) {
            this.saveUser(
                User(
                    uid = UUID.randomUUID().toString(),
                    email = firebaseEmail,
                    displayName = decodedToken.name,
                    token = token,
                    role = UserRole.MEMBER,
                    createdAt = Date.from(Instant.now()),
                    lastLogin = Date.from(Instant.now()),
                    profilePhoto = decodedToken.picture,
                    recipes = mutableListOf(),
                )
            )
        }

        return (firebaseEmail != null && firebaseEmail.equals(email, ignoreCase = true))
    }

    fun getRecipes(user: User): List<Recipe> {
        return user.recipes
    }
}
