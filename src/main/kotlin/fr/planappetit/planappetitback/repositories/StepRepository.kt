package fr.planappetit.planappetitback.repositories

import fr.planappetit.planappetitback.models.recipes.usual.Step
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StepRepository : JpaRepository<Step, String> {
}