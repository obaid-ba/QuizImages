# Captures d'écran

Les captures doivent être des **captures réelles** de l'application lancée sur un émulateur ou un téléphone.
Déposez-les dans ce dossier avec les noms ci-dessous (PNG), puis cochez la case.

Dans Android Studio : *Running Devices* → icône appareil photo, ou
`adb exec-out screencap -p > captures/nom.png`.

| ✔ | Fichier à créer | Ce que la capture doit montrer |
|:-:|---|---|
| ✔ | `1_question.png` | Une question avec son image, ses trois boutons, le score en haut à droite et la barre de progression |
| ✔ | `2_bonne_reponse.png` | Une **bonne** réponse : score mis à jour (+1), progression +1, message « ✔ Bonne réponse » |
| ✔ | `3_mauvaise_reponse.png` | Une **mauvaise** réponse : score inchangé, progression +1, bonne réponse indiquée |
| ✔ | `4_resultat_final.png` | L'écran final avec le score obtenu et le bouton Rejouer |
| ✔ | `5_petit_ecran.png` *(conseillé)* | Test sur petit écran : tous les choix restent accessibles |

Une fois les images ajoutées, vous pouvez les afficher dans le compte rendu
(`compte-rendu/compte-rendu.md`) avec `![légende](../captures/1_question.png)`.
