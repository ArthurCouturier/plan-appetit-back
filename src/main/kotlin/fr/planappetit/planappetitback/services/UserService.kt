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
    fun authenticateAndSyncUser(
        email: String,
        bearerToken: String
    ): User {
        val token = bearerToken.removePrefix("Bearer ")
        val decodedToken: FirebaseToken = firebaseService.getFirebaseTokenFromAuthToken(token)
            ?: throw UnCheckedIdentityException("Token Firebase invalide.")

        val firebaseEmail = decodedToken.email
            ?: throw UnCheckedIdentityException("Email introuvable dans le token Firebase.")

        if (!firebaseEmail.equals(email, ignoreCase = true)) {
            throw UnCheckedIdentityException("L'email ne correspond pas à l'identité Firebase.")
        }

        val user = saveOrUpdateUser(
            User(
                uid = UUID.randomUUID().toString(),
                email = firebaseEmail,
                displayName = decodedToken.name ?: "",
                token = token,
                provider = "",
                role = UserRole.MEMBER,
                createdAt = Date.from(Instant.now()),
                lastLogin = Date.from(Instant.now()),
                profilePhoto = decodedToken.picture,
                recipes = mutableListOf()
            )
        )
        return user
    }

    fun getUserByUid(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun saveOrUpdateUser(newUser: User): User {
        val existingUser = getUserByEmail(newUser.email)
        val userToSave = existingUser?.apply {
            token = newUser.token
            profilePhoto = newUser.profilePhoto
            lastLogin = Date.from(Instant.now())
        }
            ?: newUser

        if (userToSave.premiumAdvantages == null) {
            userToSave.premiumAdvantages = PremiumAdvantages(user = userToSave).apply {
                if (userToSave.role > UserRole.MEMBER) {
                    remainingGeneratedRecipes = 1000
                }
            }
        }
        return userRepository.save(userToSave)
    }

    fun deleteUser(uid: String) {
        userRepository.deleteById(uid)
    }

    fun getRecipesForUser(user: User): List<Recipe> {
        return user.recipes
    }
}
