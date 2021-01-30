package com.mortarifabio.desafiofirebase.authentication

import com.google.firebase.auth.AuthResult
import com.mortarifabio.desafiofirebase.model.User

sealed class AuthenticationResponse {
    class Success(val result: AuthResult?, val user: User? = null) : AuthenticationResponse()
    class Error(val message: String) : AuthenticationResponse()
}
