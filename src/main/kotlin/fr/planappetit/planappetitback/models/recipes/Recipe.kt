package fr.planappetit.planappetitback.models.recipes

import fr.planappetit.planappetitback.enums.CourseEnum
import fr.planappetit.planappetitback.enums.SeasonEnum
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "",
    @OneToMany(mappedBy = "recipe")
    var ingredients: List<Ingredient> = listOf(),
    var covers: Int = 0,
    var buyPrice: Number = 0.0,
    var sellPrice: Number = 0.0,
    var promotion: Number = 0.0,
    var course: CourseEnum = CourseEnum.MAIN,
    @OneToMany(mappedBy = "recipe")
    var steps: List<Step> = listOf(),
    var season: SeasonEnum = SeasonEnum.ALL
)
