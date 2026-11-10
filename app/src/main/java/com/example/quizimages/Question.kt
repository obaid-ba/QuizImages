package com.example.quizimages

import androidx.annotation.DrawableRes

/**
 * Une question du quiz.
 *
 * @property texte             énoncé de la question (elle porte sur le contenu de l'image)
 * @property imageRes          identifiant de ressource de l'image, par exemple R.drawable.triangle
 * @property choix             exactement trois réponses possibles
 * @property indexBonneReponse indice (0, 1 ou 2) de la bonne réponse dans [choix]
 * @property descriptionImage  texte lu par les lecteurs d'écran (accessibilité)
 */
class Question(
    val texte: String,
    @DrawableRes val imageRes: Int,
    val choix: Array<String>,
    val indexBonneReponse: Int,
    val descriptionImage: String
) {
    init {
        require(choix.size == 3) { "Une question doit avoir exactement trois choix" }
        require(indexBonneReponse in 0..2) { "La bonne réponse doit être l'indice 0, 1 ou 2" }
    }
}
