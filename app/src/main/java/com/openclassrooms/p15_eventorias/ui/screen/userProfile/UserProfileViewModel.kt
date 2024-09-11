package com.openclassrooms.p15_eventorias.ui.screen.userProfile

import androidx.lifecycle.ViewModel
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor (
    private val userRepository: UserRepository
): ViewModel() {



}