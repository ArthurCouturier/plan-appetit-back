package fr.planappetit.planappetitback.services

import fr.planappetit.planappetitback.enums.UserRole
import fr.planappetit.planappetitback.models.users.PremiumAdvantages
import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.repositories.PremiumAdvantagesRepository
import fr.planappetit.planappetitback.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val premiumAdvantagesRepository: PremiumAdvantagesRepository,
) {

    fun findUserByUid(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun  findAll(): List<User> {
        return userRepository.findAll()
    }

    fun saveUser(user: User): User {
        var advantages: PremiumAdvantages? = premiumAdvantagesRepository.findByUser(user)
        if (advantages == null) {
            advantages = PremiumAdvantages(user = user)
            if (user.role > UserRole.MEMBER) {
                advantages.remainingGeneratedRecipes = 1000
            }
        }
        val userCreated: User =  userRepository.save(user)
        premiumAdvantagesRepository.save(advantages)
        return userCreated
    }

    fun deleteUserByUid(uid: String) {
        userRepository.deleteById(uid)
    }
}
