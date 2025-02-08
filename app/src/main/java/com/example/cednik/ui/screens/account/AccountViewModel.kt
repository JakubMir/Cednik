package com.example.cednik.ui.screens.account

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nedim.maildroidx.MaildroidX
import co.nedim.maildroidx.MaildroidXType
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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider

) : ViewModel(), AccountActions {

    private var data: AccountScreenData = AccountScreenData()

    private var correctCredentials: Boolean = false


    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _accountUIState: MutableStateFlow<AccountUIState> =
        MutableStateFlow(AccountUIState.Loading())

    val accountUIState = _accountUIState.asStateFlow()


    fun loadUser() {
        viewModelScope.launch {
            data.user.email = dataStore.getString(DataStoreConstants.EMAIL_KEY.name)!!
            data.user.password = dataStore.getString(DataStoreConstants.PASSWORD_KEY.name)!!
            data.user.id = dataStore.getLong(DataStoreConstants.USER_KEY.name)
            _accountUIState.update {
                AccountUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun emailChanged(email: String?) {
        data.emailError = if (email == null) textError else null
        data.user.email = email ?: ""
        _accountUIState.update {
            AccountUIState.ScreenDataChanged(data)
        }
    }

    override fun passwordChanged(password: String?) {
        data.passwordError = if (password == null) textError else null
        data.user.password = password ?: ""
        _accountUIState.update {
            AccountUIState.ScreenDataChanged(data)
        }
    }

    override fun confirmChanges() {
        if (data.user.email.isNotEmpty() && data.user.password.isNotEmpty()) {
            if (correctCredentials) {
                viewModelScope.launch {
                    var oldEmail = dataStore.getString(DataStoreConstants.EMAIL_KEY.name)
                    var oldPassword = dataStore.getString(DataStoreConstants.PASSWORD_KEY.name)

                    if (oldEmail != data.user.email) {
                        sendNotificationEmail(
                            data.user.email,
                            stringResourcesProvider.getString(R.string.new_account_email)
                        )
                        sendNotificationEmail(
                            oldEmail!!,
                            stringResourcesProvider.getString(
                                R.string.changed_account_email,
                                data.user.email
                            )
                        )
                        dataStore.putString(DataStoreConstants.EMAIL_KEY.name, data.user.email)
                    }
                    if (oldPassword != data.user.password) {
                        sendNotificationEmail(
                            data.user.email,
                            stringResourcesProvider.getString(R.string.changed_account_password)
                        )
                        dataStore.putString(DataStoreConstants.PASSWORD_KEY.name, data.user.password)
                    }
                    if (oldEmail == data.user.email && oldPassword == data.user.password){
                        _accountUIState.update {
                            AccountUIState.ScreenDataChanged(data)
                        }
                    }
                    else {
                        repository.updateUser(data.user)
                        _accountUIState.update {
                            AccountUIState.ChangedCredentials()
                        }
                    }
                }
            } else {
                isEmailUsed(data.user.email)
            }
        } else {
            if (data.user.email.isEmpty()) {
                data.emailError = textError
            }
            if (data.user.password.isEmpty()) {
                data.passwordError = textError
            }

            _accountUIState.update {
                AccountUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun logOut() {
        viewModelScope.launch {
            dataStore.clearAll()
        }
        _accountUIState.update {
            AccountUIState.LoggedOut()
        }
    }


    private fun isEmailUsed(email: String) {
        viewModelScope.launch {
            repository.isEmailUsed(email).collect {
                if (it && data.user.email != dataStore.getString(DataStoreConstants.EMAIL_KEY.name)) {
                    data.emailError =
                        stringResourcesProvider.getString(R.string.email_is_already_used)
                    _accountUIState.update {
                        AccountUIState.ScreenDataChanged(data)
                    }
                } else {
                    if (!correctCredentials) {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            correctCredentials = true
                            confirmChanges()
                        } else {
                            data.emailError = stringResourcesProvider.getString(R.string.invalid_email)
                            _accountUIState.update {
                                AccountUIState.ScreenDataChanged(data)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun sendNotificationEmail(email: String, text: String) {
        MaildroidX.Builder()
            .smtp("smtp-relay.brevo.com")
            .smtpUsername("76beac001@smtp-brevo.com")
            .smtpPassword("LHRwTbAQa1VfEBxP")
            .port("587")
            .type(MaildroidXType.HTML)
            .to(email)
            .from("xmir5344@gmail.com")
            .subject(stringResourcesProvider.getString(R.string.account_details_changed))
            .body("<h1>$text</h1>")
            .isStartTLSEnabled(false)
            .onCompleteCallback(object : MaildroidX.onCompleteCallback {
                override val timeout: Long = 3000
                override fun onSuccess() {
                    Log.d("MaildroidX", "SUCCESS")
                }

                override fun onFail(errorMessage: String) {
                    Log.d("MaildroidX", "FAIL")
                }
            })
            .mail()

    }

    fun returnBack() {
        _accountUIState.update {
            AccountUIState.ReturnBack()
        }
    }

    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    fun changeLanguage(context: Context, languageCode: String) {
        viewModelScope.launch {
            dataStore.putString(DataStoreConstants.LANGUAGE_KEY.name, languageCode)
        }
        val config = context.resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale)
        else
            config.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.createConfigurationContext(config)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)


       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    languageCode
                )
            )
        }*/

        (context as? Activity)?.recreate()

    }

}