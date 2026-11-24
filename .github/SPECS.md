# Cahier des Charges : CarbuCheap France

## 🎯 Vision Produit
**CarbuCheap France** est une application Android minimaliste qui permet aux conducteurs de localiser, en temps réel et en un clic, les stations-service les moins chères à proximité immédiate de leur position.

*   **Objectif Principal :** Expérience ultra-rapide et simple, centrée sur l'information tarifaire immédiate.
*   **Cible Utilisateur :** Conducteurs soucieux d'optimiser leur budget carburant.

---

## ✨ Spécifications Fonctionnelles

### A. Fonctionnalités Clés
*   **Géolocalisation :** Récupération automatique de la position GPS (Fused Location Provider), avec repli sur la dernière position connue.
*   **Recherche Géographique :** Requête à l'API Open Data gouvernementale avec filtrage `within_distance` côté serveur. Rayon par défaut : 10 km (options : 5, 10, 20, 30, 50 km).
*   **Tri des Résultats :** Stations triées par prix croissant (le moins cher en tête).
*   **Filtrage :** Sélecteur de carburant : Gazole, SP95, SP98, E10, E85, GPLc.
*   **Préférences :** Carburant et rayon mémorisés entre deux lancements (DataStore).
*   **Navigation Externe :** Un clic lance le guidage vers la station (Google Maps, sinon toute app gérant les intents `geo:`).

### B. Parcours Utilisateur (UX Flow)
1.  **Splash Screen :** Demande de la permission de localisation, avec gestion du refus et du refus définitif (renvoi vers les paramètres).
2.  **Dashboard :** Liste des stations : `[Adresse, Ville, Prix, Distance, Date de mise à jour]`. Pull-to-refresh.
3.  **Interaction :** Un clic sur une station déclenche le guidage GPS externe.

---

## ⚙️ Architecture Technique

### A. Stack
*   **Langage :** Kotlin
*   **UI :** Jetpack Compose + Material Design 3
*   **Architecture :** MVVM — `ViewModel` + `StateFlow`, état d'écran modélisé par une `sealed class` (`Loading` / `Success` / `Error`)
*   **Réseau :** Retrofit + OkHttp, désérialisation Moshi
*   **Géolocalisation :** Google Play Services — Fused Location Provider
*   **Persistance :** DataStore Preferences
*   **Navigation :** Navigation Compose type-safe (`@Serializable`)
*   **Cibles :** minSdk 26, targetSdk 37

### B. Modèle de Données
Les réponses API (`StationRecord`) sont mappées vers un modèle de domaine immuable :

```kotlin
data class Station(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val fuelType: String,
    val distance: Double, // km, à vol d'oiseau (Haversine)
    val lastUpdate: String
)
```

---

## 🚀 Contraintes et Performance

*   **Mise en Cache :** Cache mémoire des résultats, invalidé après 15 minutes ou si l'utilisateur s'est déplacé de plus d'1 km.
*   **Concurrence :** Une seule requête de chargement active à la fois ; tout nouveau filtre annule la précédente.
*   **Latence Cible :** Temps de réponse global < 2 secondes sur réseau 4G.
*   **Robustesse :** Gestion des erreurs (API indisponible, GPS indisponible, permission refusée).
*   **Confidentialité :** Aucune donnée personnelle collectée ni transmise ; la position sert uniquement à la requête API.

---

## 📚 Annexes
*   **Source API :** [data.economie.gouv.fr — Prix des carburants, flux instantané v2](https://data.economie.gouv.fr/explore/dataset/prix-des-carburants-en-france-flux-instantane-v2/)
*   **Licence des données :** Licence Ouverte / Open Licence (Etalab)
