package com.example.preferences.main

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.preferences.R
import com.example.preferences.data.UserPreferences
import com.example.preferences.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class MainViewModel: ViewModel() {

    private val mState: MutableState<MainState> = mutableStateOf(MainState())
    val state: State<MainState> get() = mState

    private lateinit var userPreferences: UserPreferences // This instance can be removed with the help of Dagger Hilt

    // Function that receive the given context
    // In this case is the MainActivity context
    fun passContext(context: Application) {
        userPreferences = UserPreferences(context)
        getUserData()
    }

    private fun getUserData() {
        mState.value = MainState(isCharging = true)
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.user.collect{ user->
                if (user.name.isNotEmpty() && user.lastNameOne.isNotEmpty() && user.lastNameTwo.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        mState.value =
                            MainState(User(user.name, user.lastNameOne, user.lastNameTwo))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mState.value = MainState(User(), info = R.string.database_empty)
                    }
                }
            }
        }
    }

    fun saveUserData(name: String, lastNameOne: String, lastNameTwo: String) {
        val error: Int? = if (name.isEmpty()||lastNameOne.isEmpty()||lastNameTwo.isEmpty()) {
            R.string.empty_fields
        } else if (!Pattern.compile("\\A\\w{4,20}\\z", Pattern.CASE_INSENSITIVE).matcher(name).matches()) {
            R.string.name_error
        } else if (!Pattern.compile("\\A\\w{4,20}\\z", Pattern.CASE_INSENSITIVE).matcher(lastNameOne).matches()
            || !Pattern.compile("\\A\\w{4,20}\\z", Pattern.CASE_INSENSITIVE).matcher(lastNameTwo).matches()) {
            R.string.last_name_error
        } else null

        error?.let {
            mState.value = mState.value.copy(error = it)
            return
        }

        mState.value = mState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.setUserData(name, lastNameOne, lastNameTwo)
            mState.value = mState.value.copy(isLoading = false, info = R.string.success)
        }
    }

    fun cleanAll() {
        mState.value = mState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.setUserData("", "", "")
            withContext(Dispatchers.Main) {mState.value = mState.value.copy(user = User(), isLoading = false, info = R.string.clean_success)}
        }
    }

    fun hideError() {
        mState.value = mState.value.copy(error = null)
    }

    fun hideInfo() {
        mState.value = mState.value.copy(info = null)
    }

}