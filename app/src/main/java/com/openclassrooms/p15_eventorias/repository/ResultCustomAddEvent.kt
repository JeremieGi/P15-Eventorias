package com.openclassrooms.p15_eventorias.repository

/**
 * Classe créée pour faire la distinction entre un échec d'ajout du à une mauvaise adresse ou à une connexion réseau qui échoue
 */
sealed class ResultCustomAddEvent<out T> {

    data object Loading : ResultCustomAddEvent<Nothing>()

    data class Failure(
        val error : String? = null,
    ) : ResultCustomAddEvent<Nothing>()

    data class AdressFailure(
        val errorAddress: String? = null,
    ) : ResultCustomAddEvent<Nothing>()

    data class DateFailure(
        val errorDate: String? = null,
    ) : ResultCustomAddEvent<Nothing>()

    // C'est une classe de données générique qui stocke le résultat de l'opération en cas de succès.
    // Elle prend un type générique R pour permettre de représenter différents types de résultats.
    data class Success<out R>(
        val value: R // Permet de récupérer les valeurs de ResultBankAPI
    ) : ResultCustomAddEvent<R>()

}