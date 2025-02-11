package fr.planappetit.planappetitback.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class FirebaseService(
) {

    fun getFirebaseTokenFromAuthToken(token: String): FirebaseToken? {
        return try {
            val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
            decodedToken
        } catch (ex: FirebaseAuthException) {
            return null
        } catch (ex: Exception) {
            return null
        }
    }
}
