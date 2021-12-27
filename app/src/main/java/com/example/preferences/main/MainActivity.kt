package com.example.preferences.main

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.preferences.R
import com.example.preferences.presentation.components.*
import com.example.preferences.ui.theme.DataStorePreferencesTheme

// First we have to add the DataStore UserPreferences implementation to gradle file in order to use DataStore
// implementation("androidx.datastore:datastore-preferences:1.0.0") 1/11/2021

// Then we have to add the Kotlin Coroutines implementation to gradle file
// implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2' 1/11/2021
// implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2' 1/11/2021
// implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2" 1/11/2021

// Finally we have to add Compose ViewModel implementation to gradle file
// implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0" 1/11/2021

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel = viewModel()
            viewModel.passContext(applicationContext as Application)
            DataStorePreferencesTheme {
                MainScreen(
                    viewModel.state,
                    viewModel::saveUserData,
                    viewModel::cleanAll,
                    viewModel::hideError,
                    viewModel::hideInfo,
                )
            }
        }
    }

    @Composable
    fun MainScreen(
        state: State<MainState>,
        onSet: (String, String, String) -> Unit,
        onClean: ()->Unit,
        onDismiss: () -> Unit,
        onHide: () -> Unit
    ) {

        val name = remember { mutableStateOf(state.value.user.name) }
        val lastNameOne = remember { mutableStateOf(state.value.user.lastNameOne) }
        val lastNameTwo = remember { mutableStateOf(state.value.user.lastNameTwo) }
        val focusManager = LocalFocusManager.current
        var question by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.value.isCharging) {

                        CustomTextField(
                            textFieldValue = name,
                            textLabel = stringResource(R.string.name_hint),
                            keyboardType = KeyboardType.Text,
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            imeAction = ImeAction.Next
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        CustomTextField(
                            textFieldValue = lastNameOne,
                            textLabel = stringResource(R.string.last_name_one_hint),
                            keyboardType = KeyboardType.Text,
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            imeAction = ImeAction.Next
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        CustomTextField(
                            textFieldValue = lastNameTwo,
                            textLabel = stringResource(R.string.last_name_two_hint),
                            keyboardType = KeyboardType.Text,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onSet(
                                        name.value, lastNameOne.value, lastNameTwo.value
                                    )
                                }
                            ),
                            imeAction = ImeAction.Done
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        CustomButton(
                            text = stringResource(R.string.save),
                            displayProgressBar = state.value.isLoading,
                            onClick = {
                                onSet(
                                    name.value, lastNameOne.value, lastNameTwo.value
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        ClickableText(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append(stringResource(R.string.clean))
                                }
                            },
                            onClick = { question = !question }
                        )

                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
            if (question) {
                YesNoDialog(message = R.string.question, onDismiss = { question = !question }, onAccept = { onClean(); question = !question; name.value = ""; lastNameOne.value = ""; lastNameTwo.value = ""})
            }
            if (state.value.error != null) {
                state.value.error?.let { error ->
                    EventDialog(errorMessage = error, onDismiss = { onDismiss() })
                }
            }
            if (state.value.info != null) {
                state.value.info?.let { info ->
                    InfoDialog(message = info, onDismiss = { onHide() })
                }
            }
        }
    }
}