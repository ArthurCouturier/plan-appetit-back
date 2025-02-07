package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.users.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

}
