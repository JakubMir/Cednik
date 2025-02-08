package com.example.cednik.ui.screens.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nedim.maildroidx.MaildroidX
import co.nedim.maildroidx.MaildroidXType
import com.example.cednik.BuildConfig
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
class SignUpViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider

) : ViewModel(), SignUpActions {

    private var data: SignUpScreenData = SignUpScreenData()
    private var correctCredentials: Boolean = false

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _signUpUIState: MutableStateFlow<SignUpUIState> =
        MutableStateFlow(SignUpUIState.Loading())

    val signUpUIState = _signUpUIState.asStateFlow()


    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.user.email = email ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordChanged(password: String?) {
        data.passwordError = if (password == null) textError else null
        data.user.password = password ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordAgainChanged(password: String?) {
        data.passwordAgainError = if (password == null) textError else null
        data.passwordAgain = password ?: ""
        _signUpUIState.update {
            SignUpUIState.ScreenDataChanged(data)
        }
    }


    private fun isEmailUsed(email: String) {
        viewModelScope.launch {
            repository.isEmailUsed(email).collect {
                if (it) {
                    data.emailError = stringResourcesProvider.getString(R.string.email_is_already_used)
                    _signUpUIState.update {
                        SignUpUIState.ScreenDataChanged(data)
                    }
                }
                else{
                    if (!correctCredentials){
                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            correctCredentials = true
                            signUp()
                        }
                        else{
                            data.emailError = stringResourcesProvider.getString(R.string.invalid_email)
                            _signUpUIState.update {
                                SignUpUIState.ScreenDataChanged(data)
                            }
                        }
                    }

                }
            }
        }
    }


    override fun signUp() {
        if (data.user.email.isNotEmpty() && data.user.password.isNotEmpty() &&
            data.user.password == data.passwordAgain && data.passwordAgain.isNotEmpty()
        ) {
            if (correctCredentials) {
                viewModelScope.launch {
                    dataStore.putString(DataStoreConstants.EMAIL_KEY.name,data.user.email)
                    dataStore.putString(DataStoreConstants.PASSWORD_KEY.name,data.user.password)
                    repository.insertUser(data.user)
                    repository.getUser(data.user.email, data.user.password).collect {
                        dataStore.putLong(DataStoreConstants.USER_KEY.name, it.id!!)
                        sendEmail(data.user.email)
                        _signUpUIState.update {
                            SignUpUIState.SignedUp()
                        }
                    }
                }
            }
            else{
                isEmailUsed(data.user.email)
            }
        }
       else{
            if (data.user.email.isEmpty()){
                data.emailError = textError
            }
            if (data.user.password.isEmpty()){
                data.passwordError = textError
            }
            if (data.passwordAgain.isEmpty()){
                data.passwordAgainError = textError
            }
            else if (data.user.password != data.passwordAgain){
                data.passwordAgainError = stringResourcesProvider.getString(R.string.passwords_must_match)
            }

            _signUpUIState.update {
                SignUpUIState.ScreenDataChanged(data)
            }
        }
    }

    private fun sendEmail(email: String){
        MaildroidX.Builder()
            .smtp(BuildConfig.SMTP_URL)
            .smtpUsername(BuildConfig.SMTP_USERNAME)
            .smtpPassword(BuildConfig.SMTP_PASSWORD)
            .port("587")
            .type(MaildroidXType.HTML)
            .to(email)
            .from(BuildConfig.SMTP_FROM_MAIL)
            .subject(stringResourcesProvider.getString(R.string.welcome_to_cednik))
            .body(stringResourcesProvider.getString(R.string.welcome_to_cednik_mail))
            .isStartTLSEnabled(false)
            .onCompleteCallback(object : MaildroidX.onCompleteCallback{
                override val timeout: Long = 3000
                override fun onSuccess() {
                    Log.d("MaildroidX",  "SUCCESS")
                }
                override fun onFail(errorMessage: String) {
                    Log.d("MaildroidX",  "FAIL")
                }
            })
            .mail()

    }

    fun returnBack(){
        _signUpUIState.update {
            SignUpUIState.ReturnBack()
        }
    }

}