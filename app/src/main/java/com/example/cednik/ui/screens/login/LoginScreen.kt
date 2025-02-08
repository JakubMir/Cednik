package com.example.cednik.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.navigation.INavigationRouter

@Composable
fun LoginScreen(navigationRouter: INavigationRouter) {
    val viewModel = hiltViewModel<LoginViewModel>()

    val state = viewModel.loginUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(LoginScreenData())
    }


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        viewModel.isLogged()
    }

    state.value.let {
        when (it) {
            is LoginUIState.Loading -> {
                viewModel.isLogged()
            }

            is LoginUIState.Logged -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToJourneyListScreen()
                }
            }

            is LoginUIState.NavigateToPasswordRecovery -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToPasswordRecovery()
                }
                viewModel.isLogged()
            }

            is LoginUIState.NavigateToSignUp -> {
                LaunchedEffect(it) {
                    navigationRouter.navigateToSignUp()
                }
                viewModel.isLogged()
            }

            is LoginUIState.ScreenDataChanged -> {
                data = it.data
            }

            is LoginUIState.Success -> {

            }
        }
    }

    Scaffold {
        LoginScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun LoginScreenContent(
    paddingValues: PaddingValues,
    viewModel: LoginViewModel,
    data: LoginScreenData
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .weight(0.5f)
        ) {
            // Drawable logo to ImageVector (we don't need recomposition on logo)
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = {
                        Text(text = "E-mail")
                    },
                    value = data.user.email,
                    onValueChange = {
                        viewModel.emailChanged(it)
                    },
                    supportingText = {
                        if (data.emailError != null) {
                            Text(text = data.emailError!!)
                        }
                    },
                    isError = data.emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = {
                        Text(text = stringResource(R.string.password))
                    },
                    value = data.user.password,
                    onValueChange = {
                        viewModel.passwordChanged(it)
                    },
                    supportingText = {
                        if (data.passwordError != null) {
                            Text(text = data.passwordError!!)
                        }
                    },
                    isError = data.passwordError != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        // Localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        // Toggle button to hide or display password
                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
                        }
                    }
                )

                Button(
                    onClick = {
                        viewModel.login()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.log_in))
                }

                TextButton(
                    onClick = {
                        viewModel.forgotPassword()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.forgot_password))
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {

            OutlinedButton(
                onClick = {
                    viewModel.signUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Bottom)
            ) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }

}
