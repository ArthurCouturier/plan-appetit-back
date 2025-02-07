package fr.planappetit.planappetitback.services

import fr.planappetit.planappetitback.models.users.User
import fr.planappetit.planappetitback.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun findUserByUid(uid: String): User? {
        return userRepository.findById(uid).orElse(null)
    }

    fun  findAll(): List<User> {
        return userRepository.findAll()
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    fun deleteUserByUid(uid: String) {
        userRepository.deleteById(uid)
    }
}
