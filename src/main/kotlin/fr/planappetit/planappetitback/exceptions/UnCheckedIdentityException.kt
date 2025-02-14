package fr.planappetit.planappetitback.exceptions

class UnCheckedIdentityException : IllegalCallerException("User identity not validated so he cannot perform this action")
