# Cahier des Charges : CarbuCheap France

## 🎯 Vision Produit
**CarbuCheap France** est une application mobile minimaliste visant à permettre aux conducteurs de localiser, en temps réel et en un clic, les stations-service les moins chères en France, à proximité immédiate de leur position.

*   **Objectif Principal :** Offrir une expérience utilisateur ultra-rapide et simple, priorisant l'accès à l'information tarifaire immédiate.
*   **Cible Utilisateur :** Conducteurs soucieux d'optimiser leur budget carburant.

---

## ✨ Spécifications Fonctionnelles (MVP)

### A. Fonctionnalités Clés
*   **Géolocalisation :** Récupération automatique des coordonnées GPS (Latitude/Longitude) de l'utilisateur.
*   **Recherche Géographique :** Requête auprès de l'API Open Data gouvernementale, avec un rayon de recherche par défaut de 10 km.
*   **Tri des Résultats :** Affichage obligatoire des stations triées par prix croissant (le moins cher en tête).
*   **Navigation Externe :** Bouton d'action pour lancer la navigation directe vers Google Maps ou Waze avec les coordonnées GPS de la station sélectionnée.
*   **Filtrage :** Sélecteur de type de carburant obligatoire (Gazole, SP95, SP98, E85, GPL).

### B. Parcours Utilisateur (UX Flow)
1.  **Splash Screen :** Initialisation de l'application et vérification des permissions GPS.
2.  **Dashboard Principal :** Affichage d'une liste épurée des stations contenant : `[Nom, Ville, Prix, Distance]`.
3.  **Interaction :** Un clic sur une station doit déclencher le lancement du guidage GPS externe.

---

## ⚙️ Architecture Technique

### A. Stack Technologique
*   **Langage :** Dart (Typage strict requis).
*   **Gestion d'État :** Riverpod ou Bloc.
*   **Réseau :** `dio` ou `http` pour les requêtes REST.
*   **Géolocalisation :** `geolocator`.
*   **Navigation Externe :** `url_launcher`.
*   **Sérialisation :** `json_serializable` pour un typage robuste.

### B. Modèle de Données (Data Contract)
Toute réponse API doit être mappée sur une classe Dart immuable.

\`\`\`dart
class FuelStation {
  final String id;
  final String adresse;
  final String ville;
  final double prix;
  final String carburant;
  final double lat;
  final double lon;

  FuelStation({
    required this.id,
    required this.adresse,
    required this.ville,
    required this.prix,
    required this.carburant,
    required this.lat,
    required this.lon,
  });
}
\`\`\`

---

## 🚀 Contraintes et Performance

*   **Mise en Cache :** Implémenter un stockage local des résultats (TTL de 15 minutes) pour minimiser les appels API.
*   **Latence Cible :** Temps de réponse global < 2 secondes sur réseau 4G.
*   **Robustesse :** Gestion systématique des erreurs (API indisponible, perte de signal GPS).

---

## 🗺️ Roadmap de Développement

| Phase | Tâche Principale |
| :--- | :--- |
| **Phase 1** | Configuration du projet, gestion des permissions GPS, et test des requêtes API (WKT). |
| **Phase 2** | Implémentation du modèle de données (Typage fort) et du Service API. |
| **Phase 3** | Développement de l'UI (Dashboard) et intégration de la géolocalisation. |
| **Phase 4** | Intégration du bouton de navigation externe (Google Maps/Waze). |
| **Phase 5** | Tests de robustesse et finalisation de la gestion des erreurs. |

---

## 📚 Annexes
*   **Source API :** data.economie.gouv.fr
*   **Licence :** Open Data (Open Database License).