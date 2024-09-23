package com.openclassrooms.p15_eventorias.ui.screen.launch

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.p15_eventorias.model.User
import com.openclassrooms.p15_eventorias.repository.event.EventRepository
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor (
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
): ViewModel() {


    fun getCurrentUser() : User? {
        return userRepository.getCurrentUser()
    }

    fun insertCurrentUserInFirestore() {
        return userRepository.insertCurrentUser()
    }


}
