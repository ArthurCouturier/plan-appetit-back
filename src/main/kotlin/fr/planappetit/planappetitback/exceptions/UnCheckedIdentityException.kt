package fr.planappetit.planappetitback.exceptions

class UnCheckedIdentityException(string: String) : IllegalCallerException("User identity not validated so he cannot perform this action")
