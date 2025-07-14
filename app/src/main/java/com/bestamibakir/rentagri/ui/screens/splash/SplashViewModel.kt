package com.bestamibakir.rentagri.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.bestamibakir.rentagri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return userRepository.isUserLoggedIn()
    }
} 