package com.openclassrooms.p15_eventorias.ui.screen.userProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p15_eventorias.repository.ResultCustom
import com.openclassrooms.p15_eventorias.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor (
    private val userRepository: UserRepository
): ViewModel() {

    // StateFlow pour charger les données utilisateur
    private val _uiState = MutableStateFlow<UserUIState>(UserUIState.IsLoading)
    val uiState: StateFlow<UserUIState> = _uiState.asStateFlow() // Accès en lecture seule de l'extérieur


    fun loadCurrentUser() {

        viewModelScope.launch {

            userRepository.loadCurrentUser().collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiState.value = UserUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiState.value = UserUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val user = resultFlow.value
                        _uiState.value = UserUIState.Success(user)

                    }


                }

            }

        }


    }
}