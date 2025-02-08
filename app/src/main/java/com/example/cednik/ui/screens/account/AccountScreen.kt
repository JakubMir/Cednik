package com.example.cednik.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.navigation.Destination
import com.example.cednik.navigation.INavigationRouter
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navigationRouter: INavigationRouter) {
    val viewModel = hiltViewModel<AccountViewModel>()

    val state = viewModel.accountUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(AccountScreenData())
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    state.value.let {
        when (it) {
            is AccountUIState.ChangedCredentials -> {
                var text = stringResource(R.string.account_has_been_updated)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        text,
                        withDismissAction = true
                    )
                }
            }

            is AccountUIState.Loading -> {
                viewModel.loadUser()
            }

            is AccountUIState.LoggedOut -> {
                // clear stack and return do start destination
                navigationRouter.getNavController().navigate(Destination.LoginScreen.route){
                    popUpTo(Destination.LoginScreen.route){ inclusive = true }
                }
            }

            is AccountUIState.ReturnBack -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }

            is AccountUIState.ScreenDataChanged -> {
                data = it.data
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.your_account))
            },
            navigationIcon = {
                IconButton(onClick = {
                    viewModel.returnBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            },
            actions = {
                IconButton(onClick = {
                    viewModel.logOut()
                }) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "")
                }
            }
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.confirmChanges()
                },
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    dismissActionContentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        AccountScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun AccountScreenContent(
    paddingValues: PaddingValues,
    viewModel: AccountViewModel,
    data: AccountScreenData
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
        ){
            Column {
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
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                Text(text = stringResource(R.string.language), fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                Button(
                    onClick = {
                        viewModel.changeLanguage(context,"cs")
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = "Čeština")
                }

                Button(
                    onClick = {
                        viewModel.changeLanguage(context,"en")
                    }
                ) {
                    Text(text = "English")
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        ){
        Text(
            text = stringResource(R.string.app_version) +viewModel.getAppVersion(LocalContext.current),
            modifier = Modifier.align(Alignment.Bottom)
        )
        }
    }
}