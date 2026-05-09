# Changelog — CarbuCheap

Toutes les modifications notables de ce projet sont documentées ici.  
Format basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),  
versionnage selon [Semantic Versioning](https://semver.org/lang/fr/).

---

## [1.1.0] — 2026-05-09

### ✨ Améliorations UI / UX
- **Transitions animées** entre les états Chargement / Erreur / Succès du tableau de bord avec `AnimatedContent` et spring physics (Material Design 3 Expressive)
- **Animation de fondu** au lancement (splash screen) convertie en physique spring — plus fluide et conforme MD3
- **Icône de l'app** affichée à côté du titre dans la barre supérieure

### 🎨 Material Design 3 — mise en conformité complète
- Type scale corrigée selon la spec MD3 exacte (tailles, `letterSpacing`, 15 styles complets)
- Couleur `tertiary` / `onTertiary` / `tertiaryContainer` / `onTertiaryContainer` explicitement définie pour le thème clair et sombre
- `Shapes()` déclaré explicitement dans `MaterialTheme`
- `Surface` wrapper ajouté dans `CarbuCheapTheme` pour propager correctement la couleur de fond
- Suppression des anti-patterns : `.copy(alpha = 0.75f)` → token `onPrimaryContainer`, paramètres `color` redondants sur `CircularProgressIndicator`

### ♿ Accessibilité
- `semantics { contentDescription }` ajouté sur les indicateurs de chargement (Splash & Dashboard)
- `contentDescription = "Erreur"` sur l'icône d'erreur du Dashboard
- Cible tactile du bouton de navigation portée à **48dp** (minimum MD3)
- `statusBarsPadding()` / `navigationBarsPadding()` sur le SplashScreen pour l'edge-to-edge

### ⚡ Performance & Architecture
- `collectAsState()` → **`collectAsStateWithLifecycle()`** sur les 4 StateFlow du Dashboard (arrêt de la collection en arrière-plan)
- Navigation type-safe avec `@Serializable` data objects (Navigation Compose 2.8+)
- Dépendance `kotlinx-serialization-json` et plugin `kotlin.serialization` ajoutés
- Extraction de `DashboardContent` (composable stateless) pour meilleure testabilité

### 🔧 Divers
- Mémoires stables `FUEL_TYPE_LIST` / `RADIUS_OPTIONS` en top-level pour éviter les ré-allocations
- Suppression des imports inutilisés (`FontWeight` inline, `remember` non utilisé)
- CI : correction du bit exécutable de `gradlew`

---

## [1.0.2] — 2025

### Ajouté
- Politique de confidentialité + fiche Play Store (listing FR)
- Pull-to-refresh sur la liste des stations
- ProGuard / shrink resources activés en release
- Configuration de signature release via variables d'environnement
- CI GitHub Actions : workflow lint/tests/build + scan de secrets (gitleaks)
- Icône de lancement vectorielle (suppression des bitmaps mipmaps)

### Modifié
- `TopAppBar` avec `pinnedScrollBehavior` — effet de scroll sur l'entête
- Style de surbrillance de la première carte station amélioré

---

## [1.0.0] — 2025

### Ajouté
- Localisation GPS automatique (permission `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`)
- Recherche des stations-service les moins chères dans un rayon de 5 à 50 km
- 6 types de carburants : Gazole, SP95, SP98, E10, E85, GPLc
- Tri des résultats par prix croissant
- Navigation directe vers Google Maps / app de navigation
- Données en temps réel — source officielle : `data.economie.gouv.fr`
- Mise en cache intelligente (15 minutes)
- Support du mode sombre
- Material Design 3 avec couleur dynamique (Android 12+)

