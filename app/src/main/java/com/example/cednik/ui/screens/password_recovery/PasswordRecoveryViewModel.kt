package com.example.cednik.ui.screens.password_recovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nedim.maildroidx.MaildroidX
import co.nedim.maildroidx.MaildroidXType
import com.example.cednik.BuildConfig
import com.example.cednik.R
import com.example.cednik.database.ILocalJourneysRepository
import com.example.cednik.utils.StringResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val stringResourcesProvider: StringResourcesProvider

) : ViewModel(), PasswordRecoveryActions {

    private var data: PasswordRecoveryScreenData = PasswordRecoveryScreenData()
    private var correctCredentials: Boolean = false

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _passwordRecoveryUIState: MutableStateFlow<PasswordRecoveryUIState> =
        MutableStateFlow(PasswordRecoveryUIState.Loading())

    val passwordRecoveryUIState = _passwordRecoveryUIState.asStateFlow()


    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.email = email ?: ""
        _passwordRecoveryUIState.update {
            PasswordRecoveryUIState.ScreenDataChanged(data)
        }
    }

    override fun recoverPassword() {
        if (data.email.isNotEmpty()){
            if (correctCredentials){
                viewModelScope.launch {
                    repository.getUserPassword(data.email).collect{
                        sendPassword(data.email, it)

                        _passwordRecoveryUIState.update {
                            PasswordRecoveryUIState.Recovered()
                        }
                    }
                }
            }
            else{
                isEmailUsed(data.email)
            }
        }
        else{
            if (data.email.isEmpty()){
                data.emailError = textError
                _passwordRecoveryUIState.update {
                    PasswordRecoveryUIState.ScreenDataChanged(data)
                }
            }
        }
    }

    private fun sendPassword(email: String, password: String){
        MaildroidX.Builder()
            .smtp(BuildConfig.SMTP_URL)
            .smtpUsername(BuildConfig.SMTP_USERNAME)
            .smtpPassword(BuildConfig.SMTP_PASSWORD)
            .port("587")
            .type(MaildroidXType.HTML)
            .to(email)
            .from(BuildConfig.SMTP_FROM_MAIL)
            .subject(stringResourcesProvider.getString(R.string.password_recovery))
            .body(stringResourcesProvider.getString(R.string.password_mail, password))
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

    private fun isEmailUsed(email: String) {
        viewModelScope.launch {
            repository.isEmailUsed(email).collect {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    data.emailError = stringResourcesProvider.getString(R.string.invalid_email)
                    _passwordRecoveryUIState.update {
                        PasswordRecoveryUIState.ScreenDataChanged(data)
                    }
                }
                else if (!it) {
                    data.emailError =
                        stringResourcesProvider.getString(R.string.this_account_doesn_t_exist)
                    _passwordRecoveryUIState.update {
                        PasswordRecoveryUIState.ScreenDataChanged(data)
                    }
                }
                else{
                    if (!correctCredentials){
                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            correctCredentials = true
                            recoverPassword()
                        }
                    }
                }
            }
        }
    }

    fun returnBack(){
        _passwordRecoveryUIState.update {
            PasswordRecoveryUIState.ReturnBack()
        }
    }

}