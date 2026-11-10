package com.example.quizimages

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizimages.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

/**
 * Quiz en images : cinq questions statiques, trois boutons de réponse par question.
 * Toute la partie se déroule dans cette unique activité (pas de fragment).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /** Les trois boutons de choix, dans l'ordre : le bouton i correspond à choix[i]. */
    private lateinit var boutonsChoix: List<MaterialButton>

    // ---------------------------------------------------------------------------------------
    // Les cinq questions : un tableau Kotlin statique. La bonne réponse n'est pas toujours
    // au même endroit (indices 1, 0, 2, 1, 2).
    // ---------------------------------------------------------------------------------------
    private val questions: Array<Question> = arrayOf(
        Question(
            texte = "Quel est le nom de la forme affichée ?",
            imageRes = R.drawable.triangle,
            choix = arrayOf("Un carré", "Un triangle", "Un cercle"),
            indexBonneReponse = 1,
            descriptionImage = "Un triangle orange"
        ),
        Question(
            texte = "Combien d'angles droits possède cette forme ?",
            imageRes = R.drawable.carre,
            choix = arrayOf("4", "3", "2"),
            indexBonneReponse = 0,
            descriptionImage = "Un carré bleu"
        ),
        Question(
            texte = "Combien de sommets possède cette forme ?",
            imageRes = R.drawable.cercle,
            choix = arrayOf("4", "1", "0"),
            indexBonneReponse = 2,
            descriptionImage = "Un cercle jaune"
        ),
        Question(
            texte = "Combien de branches compte cette étoile ?",
            imageRes = R.drawable.etoile,
            choix = arrayOf("6", "5", "4"),
            indexBonneReponse = 1,
            descriptionImage = "Une étoile violette"
        ),
        Question(
            texte = "Qu'est-ce qui distingue cette forme d'un carré ?",
            imageRes = R.drawable.rectangle,
            choix = arrayOf(
                "Elle a trois côtés",
                "Elle a cinq côtés",
                "Ses côtés ne sont pas tous égaux"
            ),
            indexBonneReponse = 2,
            descriptionImage = "Un rectangle vert, plus large que haut"
        )
    )

    // ---------------------------------------------------------------------------------------
    // État de la partie
    // ---------------------------------------------------------------------------------------
    private var indexQuestion = 0        // indice de la question courante
    private var score = 0                // nombre de bonnes réponses
    private var nbReponses = 0           // nombre de réponses données = progression
    private var dejaRepondu = false      // la question courante a-t-elle reçu une réponse ?
    private var choixSelectionne = AUCUN_CHOIX
    private var partieTerminee = false   // le résultat final est-il affiché ?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boutonsChoix = listOf(binding.btnChoix1, binding.btnChoix2, binding.btnChoix3)

        // Un seul gestionnaire pour les trois boutons : l'indice du bouton cliqué
        // est retrouvé dans la liste, il correspond à l'indice du choix.
        boutonsChoix.forEachIndexed { index, bouton ->
            bouton.setOnClickListener { onChoixClique(index) }
        }
        binding.btnSuivant.setOnClickListener { onSuivantClique() }
        binding.btnRejouer.setOnClickListener { rejouer() }

        // Restauration après rotation de l'écran
        savedInstanceState?.let {
            indexQuestion = it.getInt(CLE_INDEX).coerceIn(0, questions.lastIndex)
            score = it.getInt(CLE_SCORE)
            nbReponses = it.getInt(CLE_NB_REPONSES)
            dejaRepondu = it.getBoolean(CLE_REPONDU)
            choixSelectionne = it.getInt(CLE_CHOIX, AUCUN_CHOIX)
            partieTerminee = it.getBoolean(CLE_TERMINEE)
        }
        afficherEtatCourant()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CLE_INDEX, indexQuestion)
        outState.putInt(CLE_SCORE, score)
        outState.putInt(CLE_NB_REPONSES, nbReponses)
        outState.putBoolean(CLE_REPONDU, dejaRepondu)
        outState.putInt(CLE_CHOIX, choixSelectionne)
        outState.putBoolean(CLE_TERMINEE, partieTerminee)
    }

    // ---------------------------------------------------------------------------------------
    // Affichage
    // ---------------------------------------------------------------------------------------

    /** Redessine l'écran à partir des variables d'état (démarrage, rotation, rejouer). */
    private fun afficherEtatCourant() {
        majBandeau()
        if (partieTerminee) {
            afficherResultatFinal()
        } else {
            afficherQuestion()
            if (dejaRepondu) afficherCorrection()
        }
    }

    /** Affiche l'image, le texte et les trois choix de la question courante (état « non répondu »). */
    private fun afficherQuestion() {
        val question = questions[indexQuestion]

        binding.groupeQuestion.visibility = View.VISIBLE
        binding.groupeResultat.visibility = View.GONE

        binding.txtNumero.text = getString(R.string.question_numero, indexQuestion + 1, questions.size)
        binding.imgQuestion.setImageResource(question.imageRes)
        binding.imgQuestion.contentDescription = question.descriptionImage
        binding.txtQuestion.text = question.texte

        boutonsChoix.forEachIndexed { index, bouton ->
            bouton.text = question.choix[index]
            bouton.isEnabled = true
            styliser(bouton, R.color.card, R.color.primary, R.color.ink)
        }

        binding.txtFeedback.visibility = View.INVISIBLE
        binding.btnSuivant.visibility = View.INVISIBLE
    }

    /**
     * Montre la correction après une réponse : texte explicite + marque ✓/✗ sur les boutons.
     * La couleur complète le message mais n'est jamais le seul indice.
     */
    private fun afficherCorrection() {
        val question = questions[indexQuestion]
        val bonneReponse = question.indexBonneReponse
        val reponseJuste = choixSelectionne == bonneReponse

        boutonsChoix.forEachIndexed { index, bouton ->
            bouton.isEnabled = false                       // les trois choix sont désactivés
            when {
                index == bonneReponse -> {
                    bouton.text = getString(R.string.marque_juste, question.choix[index])
                    styliser(bouton, R.color.correct_soft, R.color.correct, R.color.ink)
                }
                index == choixSelectionne -> {
                    bouton.text = getString(R.string.marque_faux, question.choix[index])
                    styliser(bouton, R.color.wrong_soft, R.color.wrong, R.color.ink)
                }
                else -> styliser(
                    bouton, R.color.neutral_disabled_bg, R.color.stroke, R.color.neutral_disabled_text
                )
            }
        }

        binding.txtFeedback.visibility = View.VISIBLE
        if (reponseJuste) {
            binding.txtFeedback.text = getString(R.string.feedback_juste)
            binding.txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.correct))
        } else {
            binding.txtFeedback.text =
                getString(R.string.feedback_faux, question.choix[bonneReponse])
            binding.txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.wrong))
        }

        // « Voir le résultat » à la dernière question, « Question suivante » sinon
        binding.btnSuivant.text = getString(
            if (indexQuestion == questions.lastIndex) R.string.btn_resultat else R.string.btn_suivant
        )
        binding.btnSuivant.visibility = View.VISIBLE
    }

    /** Actualise le score, la barre et le texte de progression (nombre de RÉPONSES, pas de bonnes réponses). */
    private fun majBandeau() {
        binding.txtScore.text = getString(R.string.score_format, score, questions.size)
        binding.progressQuiz.max = questions.size
        binding.progressQuiz.progress = nbReponses
        binding.progressQuiz.contentDescription =
            getString(R.string.progression_description, nbReponses, questions.size)
        binding.txtProgression.text =
            getString(R.string.progression_format, nbReponses, questions.size)
    }

    /** Masque la question et ses choix, puis affiche le score final et le bouton Rejouer. */
    private fun afficherResultatFinal() {
        binding.groupeQuestion.visibility = View.GONE
        binding.groupeResultat.visibility = View.VISIBLE
        binding.txtResultatScore.text = getString(R.string.resultat_score, score, questions.size)
        binding.txtResultatMessage.setText(
            when {
                score == questions.size -> R.string.resultat_parfait
                score >= 3 -> R.string.resultat_bien
                score >= 1 -> R.string.resultat_moyen
                else -> R.string.resultat_faible
            }
        )
    }

    /** Applique fond, contour et couleur de texte explicites (valables aussi bouton désactivé). */
    private fun styliser(bouton: MaterialButton, fond: Int, contour: Int, texte: Int) {
        bouton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fond))
        bouton.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, contour))
        bouton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, texte)))
    }

    // ---------------------------------------------------------------------------------------
    // Événements
    // ---------------------------------------------------------------------------------------

    /**
     * Clic sur l'un des trois boutons : [index] est l'indice du choix (0, 1 ou 2).
     * Le garde-fou `dejaRepondu` garantit un seul point maximum par question,
     * même en cas d'appuis répétés.
     */
    private fun onChoixClique(index: Int) {
        if (dejaRepondu || partieTerminee) return

        dejaRepondu = true
        choixSelectionne = index
        nbReponses++                                       // la progression avance dans tous les cas
        if (index == questions[indexQuestion].indexBonneReponse) {
            score++                                        // +1 uniquement si la réponse est juste
        }
        majBandeau()
        afficherCorrection()
    }

    /** Clic sur « Question suivante » / « Voir le résultat » (impossible avant d'avoir répondu). */
    private fun onSuivantClique() {
        if (!dejaRepondu || partieTerminee) return

        if (indexQuestion >= questions.lastIndex) {
            // Dernière question : on ne dépasse jamais les limites du tableau
            partieTerminee = true
            afficherResultatFinal()
        } else {
            indexQuestion++
            dejaRepondu = false
            choixSelectionne = AUCUN_CHOIX
            afficherQuestion()
            binding.scrollQuiz.scrollTo(0, 0)              // nouvelle question : on remonte en haut
        }
    }

    /** Remet quiz, score et progression à zéro et revient à la première question. */
    private fun rejouer() {
        indexQuestion = 0
        score = 0
        nbReponses = 0
        dejaRepondu = false
        choixSelectionne = AUCUN_CHOIX
        partieTerminee = false
        afficherEtatCourant()
        binding.scrollQuiz.scrollTo(0, 0)
    }

    private companion object {
        const val AUCUN_CHOIX = -1
        const val CLE_INDEX = "index_question"
        const val CLE_SCORE = "score"
        const val CLE_NB_REPONSES = "nb_reponses"
        const val CLE_REPONDU = "deja_repondu"
        const val CLE_CHOIX = "choix_selectionne"
        const val CLE_TERMINEE = "partie_terminee"
    }
}
