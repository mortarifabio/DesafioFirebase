package com.mortarifabio.desafiofirebase.authentication.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mortarifabio.desafiofirebase.authentication.AuthenticationBusiness
import com.mortarifabio.desafiofirebase.authentication.AuthenticationResponse
import com.mortarifabio.desafiofirebase.databinding.ActivityLoginBinding
import com.mortarifabio.desafiofirebase.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    private val business: AuthenticationBusiness by lazy {
        AuthenticationBusiness(getApplication())
    }
    var userLiveData = MutableLiveData<AuthenticationResponse>()

    fun getUserEmail(): String {
        return business.getUserEmail()
    }

    fun login(binding: ActivityLoginBinding) {
        viewModelScope.launch {
            userLiveData.postValue(business.login(binding))
        }
    }

    fun registerUser(binding: ActivityRegisterBinding) {
        viewModelScope.launch {
            userLiveData.postValue(business.registerUser(binding))
        }
    }

}