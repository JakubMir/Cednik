package com.example.cednik.ui.screens.password_recovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.navigation.INavigationRouter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(navigationRouter: INavigationRouter) {
    val viewModel = hiltViewModel<PasswordRecoveryViewModel>()

    val state = viewModel.passwordRecoveryUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(PasswordRecoveryScreenData())
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    state.value.let {
        when (it) {
            is PasswordRecoveryUIState.Loading -> {

            }

            is PasswordRecoveryUIState.Recovered -> {
                var text = stringResource(R.string.password_has_been_sent)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        text,
                        withDismissAction = true
                    )
                }
            }

            is PasswordRecoveryUIState.ReturnBack -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }

            is PasswordRecoveryUIState.ScreenDataChanged -> {
                data = it.data
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.password_recovery))
            },
            navigationIcon = {
                IconButton(onClick = {
                    viewModel.returnBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }
        )
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
        PasswordRecoveryScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun PasswordRecoveryScreenContent(
    paddingValues: PaddingValues,
    viewModel: PasswordRecoveryViewModel,
    data: PasswordRecoveryScreenData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            label = {
                Text(text = "E-mail")
            },
            value = data.email,
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

        Button(
            onClick = {
                viewModel.recoverPassword()
            },
        ) {
            Text(text = stringResource(R.string.recover_password))
        }
    }
}