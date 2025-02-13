package fr.planappetit.planappetitback.models.recipes.usual

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.planappetit.planappetitback.enums.CourseEnum
import fr.planappetit.planappetitback.enums.SeasonEnum
import fr.planappetit.planappetitback.models.recipes.generation.RecipeFromOpenAI
import fr.planappetit.planappetitback.models.users.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    @GeneratedValue(generator = "uuid2")
    var uuid: UUID? = null,
    var name: String = "",
    @OneToMany(mappedBy = "recipe")
    var ingredients: MutableList<Ingredient> = mutableListOf(),
    var covers: Int = 0,
    var buyPrice: Number = 0.0,
    var sellPrice: Number = 0.0,
    var promotion: Number = 0.0,
    var course: CourseEnum = CourseEnum.MAIN,
    @OneToMany(mappedBy = "recipe")
    var steps: MutableList<Step> = mutableListOf(),
    var season: List<SeasonEnum>? = listOf(),
    @ManyToOne(cascade = [CascadeType.ALL])
    @JsonIgnore
    val user : User? = null,
) {
    constructor() : this(
        uuid = null,
        name = "",
        ingredients = mutableListOf(),
        covers = 0,
        buyPrice = 0.0,
        sellPrice = 0.0,
        promotion = 0.0,
        course = CourseEnum.MAIN,
        steps = mutableListOf(),
        season = listOf(),
        user = null
    )

    constructor(recipeFromOpenAI: RecipeFromOpenAI, user: User) : this(
        uuid = null,
        name = recipeFromOpenAI.title,
        ingredients = recipeFromOpenAI.ingredients?.map { i ->
            Ingredient(i)
        } as MutableList<Ingredient>,
        covers = recipeFromOpenAI.coversNb,
        buyPrice = recipeFromOpenAI.buyPrice,
        sellPrice = recipeFromOpenAI.sellPrice,
        promotion = 0.0,
        course = CourseEnum.MAIN,
        steps = recipeFromOpenAI.steps?.map { s ->
            Step(s)
        } as  MutableList<Step>,
        season = listOf<SeasonEnum>(),
        user = user,
    ) {
        ingredients.forEach {  ingredient -> ingredient.recipe = this }
        steps.forEach {  step -> step.recipe = this }
    }
}
