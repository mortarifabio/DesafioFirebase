package com.mortarifabio.desafiofirebase.authentication

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.extensions.toHashMap
import com.mortarifabio.desafiofirebase.model.User
import com.mortarifabio.desafiofirebase.utils.Constants.Authentication.AUTH_LOG_KEY
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_COLLECTION_USERS
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_LOG_KEY
import kotlinx.coroutines.tasks.await

class AuthenticationRepository {

    private val auth by lazy {
        Firebase.auth
    }
    private val db by lazy {
        Firebase.firestore
    }

    suspend fun loginWithEmail(email: String, password: String) : AuthenticationResponse {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthenticationResponse.Success(result)
        } catch(exception: Exception) {
            Log.w(AUTH_LOG_KEY, "signInWithEmail:failure", exception)
            AuthenticationResponse.Error(exception.localizedMessage ?: "")
        }
    }

    suspend fun registerWithEmail(email: String, password: String) : AuthenticationResponse {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthenticationResponse.Success(result)
        } catch(exception: Exception) {
            Log.w(AUTH_LOG_KEY, "createUserWithEmail:failure", exception)
            AuthenticationResponse.Error(exception.localizedMessage ?: "")
        }
    }

    suspend fun saveUser(user: User) : Boolean {
        return try {
            user.uid?.let {
                val userData = user.toHashMap()
                db.collection(FIRESTORE_COLLECTION_USERS)
                    .document(it)
                    .set(userData)
                    .await()
                true
            } ?: run {
                false
            }
        } catch(exception: Exception) {
            Log.w(FIRESTORE_LOG_KEY, "saveUser:failure", exception)
            false
        }
    }
}