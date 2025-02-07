package fr.planappetit.planappetitback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlanAppetitBackApplication

fun main(args: Array<String>) {
    runApplication<PlanAppetitBackApplication>(*args)
}
