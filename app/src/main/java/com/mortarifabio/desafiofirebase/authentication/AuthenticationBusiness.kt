package com.mortarifabio.desafiofirebase.authentication

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.widget.EditText
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.model.User
import com.mortarifabio.desafiofirebase.databinding.ActivityLoginBinding
import com.mortarifabio.desafiofirebase.databinding.ActivityRegisterBinding
import com.mortarifabio.desafiofirebase.utils.Constants.Authentication.SHARED_PREFERENCES_EMAIL_KEY
import com.mortarifabio.desafiofirebase.utils.Constants.Authentication.SHARED_PREFERENCES_FILENAME

class AuthenticationBusiness(
    private val context: Application
) {

    private val repository by lazy {
        AuthenticationRepository()
    }
    private val sharedPref by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_FILENAME, MODE_PRIVATE)
    }
    private val auth by lazy {
        Firebase.auth
    }

    suspend fun login(binding: ActivityLoginBinding) : AuthenticationResponse? {
        return if(canLogin(binding)){
            val email = binding.tietLoginEmail.text.toString()
            val password = binding.tietLoginPassword.text.toString()
            when(val response = repository.loginWithEmail(email, password)){
                is AuthenticationResponse.Success -> {
                    auth.currentUser?.let {
                        val user = User(
                            uid = it.uid,
                            name = it.displayName ?: "",
                            email = it.email ?: ""
                        )
                        AuthenticationResponse.Success(response.result, user)
                    }
                }
                is AuthenticationResponse.Error -> {
                    AuthenticationResponse.Error(context.getString(R.string.authentication_failed))
                }
            }
        } else {
            null
        }
    }

    suspend fun registerUser(binding: ActivityRegisterBinding) : AuthenticationResponse? {
        return if (canRegister(binding)) {
            val email = binding.tietRegisterEmail.text.toString()
            val password = binding.tietRegisterPassword.text.toString()
            when (repository.registerWithEmail(email, password)) {
                is AuthenticationResponse.Success -> {
                    val name = binding.tietRegisterName.text.toString()
                    val user = User(name = name, email = email)
                    auth.currentUser?.let { authUser ->
                        user.uid = authUser.uid
                        saveUser(user)
                    }
                }
                is AuthenticationResponse.Error -> {
                    AuthenticationResponse.Error(context.getString(R.string.email_already_registered))
                }
            }
        } else {
            null
        }
    }

    private suspend fun saveUser(user: User): AuthenticationResponse? {
        return if(repository.saveUser(user)) {
            AuthenticationResponse.Success(null, user)
        }else{
            AuthenticationResponse.Error(context.getString(R.string.register_failed))
        }
    }

    fun getUserEmail(): String {
        val userEmail = sharedPref.getString(SHARED_PREFERENCES_EMAIL_KEY, "")
        return userEmail?.let {
            userEmail
        } ?: ""
    }

    private fun canLogin(binding: ActivityLoginBinding): Boolean {
        binding.apply {
            return isEditTextFilled(
                    tietLoginEmail,
                    tilLoginEmail,
                    context.getString(R.string.email)
                ) && isEmailValid(
                    tietLoginEmail,
                    tilLoginEmail
                ) && isEditTextFilled(
                    tietLoginPassword,
                    tilLoginPassword,
                    context.getString(R.string.password)
                )
        }
    }

    private fun canRegister(binding: ActivityRegisterBinding): Boolean {
        binding.apply {
            return isEditTextFilled(
                tietRegisterName,
                tilRegisterName,
                context.getString(R.string.name)
            ) && isEditTextFilled(
                tietRegisterEmail,
                tilRegisterEmail,
                context.getString(R.string.email)
            ) && isEmailValid(
                tietRegisterEmail,
                tilRegisterEmail
            ) && isEditTextFilled(
                tietRegisterPassword,
                tilRegisterPassword,
                context.getString(R.string.password)
            ) && isPasswordValid(
                tietRegisterPassword,
                tilRegisterPassword
            ) && isPasswordsEquals(
                tietRegisterPassword,
                tietRegisterRepeatPassword,
                tilRegisterRepeatPassword
            )
        }
    }

    private fun isEditTextFilled(et: EditText, til: TextInputLayout, label: String): Boolean {
        return if (et.text.isNotEmpty()) {
            til.isErrorEnabled = false
            true
        } else {
            til.error = context.getString(R.string.field_required, label)
            et.requestFocus()
            false
        }
    }
    private fun isEmailValid(et: EditText, til: TextInputLayout): Boolean {
        return if (EMAIL_ADDRESS.matcher(et.text.toString()).matches()) {
            til.isErrorEnabled = false
            true
        } else {
            if (et.text.isEmpty()) {
                til.error = context.getString(
                    R.string.field_required,
                    context.getString(R.string.email)
                )
            } else {
                til.error = context.getString(R.string.msg_invalid_email)
            }
            et.requestFocus()
            false
        }
    }

    private fun isPasswordValid(et: EditText, til: TextInputLayout): Boolean {
        return if (et.text.matches(Regex("^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$"))) {
            til.isErrorEnabled = false
            true
        } else {
            til.error = context.getString(R.string.password_invalid)
            et.requestFocus()
            false
        }
    }

    private fun isPasswordsEquals(et1: EditText, et2: EditText, til: TextInputLayout): Boolean {
        return if (et1.text.toString() == et2.text.toString()) {
            til.isErrorEnabled = false
            true
        } else {
            til.error = context.getString(R.string.passwords_do_not_match)
            et2.setText("")
            et2.requestFocus()
            false
        }
    }
}