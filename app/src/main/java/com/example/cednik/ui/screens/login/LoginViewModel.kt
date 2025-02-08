package com.example.cednik.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cednik.R
import com.example.cednik.database.ILocalJourneysRepository
import com.example.cednik.datastore.DataStoreConstants
import com.example.cednik.datastore.IDataStoreRepository
import com.example.cednik.utils.StringResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider

) : ViewModel(), LoginActions {

    private var data: LoginScreenData = LoginScreenData()

    private var correctCredentials: Boolean = false

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _loginUIState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState.Loading())

    val loginUIState = _loginUIState.asStateFlow()



    fun load(){
        _loginUIState.update {
            LoginUIState.Success()
        }
    }
    fun isLogged(){

        viewModelScope.launch {

            if (!dataStore.getString(DataStoreConstants.EMAIL_KEY.name).isNullOrEmpty()){
                _loginUIState.update {
                    LoginUIState.Logged()
                }
            }
            else{
                load()
            }
        }
    }

    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.user.email = email?:""
        _loginUIState.update {
            LoginUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordChanged(password: String?) {
        data.passwordError = if (password == null) textError else null
        data.user.password = password?:""
        _loginUIState.update {
            LoginUIState.ScreenDataChanged(data)
        }
    }

    override fun login() {
        if (data.user.email.isNotEmpty() && data.user.password.isNotEmpty()){
            isEmailUsed(data.user.email)
            isPasswordCorrect(data.user.email, data.user.password)

            if (correctCredentials) {
                viewModelScope.launch {
                    dataStore.putString(DataStoreConstants.EMAIL_KEY.name,data.user.email)
                    dataStore.putString(DataStoreConstants.PASSWORD_KEY.name,data.user.password)
                    repository.getUser(data.user.email, data.user.password).collect {
                        dataStore.putLong(DataStoreConstants.USER_KEY.name, it.id!!)
                        _loginUIState.update {
                            LoginUIState.Logged()
                        }
                    }
                }
            }
        } else {
            if (data.user.email.isEmpty()){
                data.emailError = textError
            }
            if (data.user.password.isEmpty()){
                data.passwordError = textError
            }

            _loginUIState.update {
                LoginUIState.ScreenDataChanged(data)
            }
        }
    }

    private fun isEmailUsed(email: String){
        viewModelScope.launch {
            repository.isEmailUsed(email).collect {
                if (!it) {
                    data.emailError = stringResourcesProvider.getString(R.string.wrong_email)
                    _loginUIState.update {
                        LoginUIState.ScreenDataChanged(data)
                    }
                }
            }
        }
    }

    private fun isPasswordCorrect(email: String, password: String){
        viewModelScope.launch {
            repository.isPasswordCorrect(email, password).collect{
                if (!it){
                    data.passwordError = stringResourcesProvider.getString(R.string.wrong_password)
                    _loginUIState.update {
                        LoginUIState.ScreenDataChanged(data)
                    }
                }
                else{
                    if (!correctCredentials){
                        correctCredentials = true
                        login()
                    }

                }
            }
        }
    }

    override fun forgotPassword() {
        _loginUIState.update {
            LoginUIState.NavigateToPasswordRecovery()
        }
    }

    override fun signUp() {
        _loginUIState.update {
            LoginUIState.NavigateToSignUp()
        }
    }

}