package com.openclassrooms.p15_eventorias.ui.screen.launch

import androidx.lifecycle.ViewModel
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor (
    private val userRepository: UserRepository
): ViewModel() {


    fun getCurrentUserID() : String {
        return userRepository.getCurrentUserID()
    }

    fun insertCurrentUser() {
        return userRepository.insertCurrentUser()
    }


}
