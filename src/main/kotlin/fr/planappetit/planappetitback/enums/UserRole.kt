package fr.planappetit.planappetitback.enums

enum class UserRole(val level: Int) {
    MEMBER(0),
    PREMIUM(1),
    ADMIN(2)
}
