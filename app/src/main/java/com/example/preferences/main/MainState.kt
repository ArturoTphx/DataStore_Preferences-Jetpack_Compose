package com.example.preferences.main

import com.example.preferences.model.User

data class MainState(
    val user: User = User(),
    val isLoading: Boolean = false,
    val isCharging: Boolean = false,
    val error: Int? = null,
    val info: Int? = null
)
