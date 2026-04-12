# Fiche Play Store — CarbuCheap (Français)

---

## Titre (max 50 caractères)

```
CarbuCheap – Prix Carburant Pas Cher
```
> 37 caractères ✓

---

## Description courte (max 80 caractères)

```
Trouvez la station la moins chère près de chez vous, en un instant.
```
> 67 caractères ✓

---

## Description complète (max 4 000 caractères)

```
⛽ Arrêtez de payer trop cher votre carburant.

CarbuCheap localise automatiquement les stations-service les moins chères autour de vous, en temps réel, grâce aux données officielles du gouvernement français.

──────────────────────────────
🔎 COMMENT ÇA MARCHE ?
──────────────────────────────
1. Ouvrez l'app — votre position GPS est détectée automatiquement.
2. Choisissez votre carburant : Gazole, SP95, SP98, E10, E85 ou GPLc.
3. Les stations les plus proches s'affichent, triées du moins cher au plus cher.
4. Cliquez sur une station pour lancer la navigation directement dans Google Maps ou votre app favorite.

──────────────────────────────
✨ FONCTIONNALITÉS
──────────────────────────────
• Géolocalisation automatique — pas de saisie d'adresse nécessaire
• 6 types de carburants : Gazole, SP95, SP98, E10, E85, GPLc
• Tri par prix croissant — le moins cher toujours en premier
• Rayon de recherche ajustable : 5, 10, 20, 30 ou 50 km
• Navigation GPS intégrée — un clic pour démarrer le guidage
• Données officielles — source : data.economie.gouv.fr (Open Data gouvernemental)
• Mise en cache intelligente — résultats disponibles 15 min sans recharger
• Mode sombre supporté

──────────────────────────────
📊 DONNÉES FIABLES
──────────────────────────────
CarbuCheap utilise exclusivement l'API officielle du Ministère de l'Économie et des Finances. Les prix sont mis à jour en temps réel par les stations elles-mêmes, conformément à la réglementation française.

Aucune publicité. Aucun abonnement. Aucune collecte de données personnelles.

──────────────────────────────
🚗 POUR QUI ?
──────────────────────────────
• Automobilistes qui font le plein régulièrement
• Professionnels de la route (livreurs, artisans, commerciaux)
• Quiconque veut faire des économies sur le carburant

Économisez plusieurs centimes par litre, plusieurs fois par semaine — ça compte !

Téléchargez CarbuCheap gratuitement et faites des économies dès aujourd'hui.
```
> ~1 850 caractères ✓

---

## Notes de version — v1.0 (Nouveautés)

```
Première version de CarbuCheap !

• Localisation GPS automatique
• Recherche des stations-service les moins chères dans un rayon de 5 à 50 km
• Support de 6 carburants : Gazole, SP95, SP98, E10, E85, GPLc
• Tri des résultats par prix croissant
• Navigation directe vers Google Maps
• Données en temps réel — source officielle gouvernementale
```

---

## Catégorie Play Store

**Catégorie principale :** Auto & Véhicules  
**Catégorie secondaire (optionnel) :** Cartes & Navigation

---

## Tags / Mots-clés (pour l'App Store Optimization)

> Ces mots-clés doivent apparaître naturellement dans la description — ne pas les coller tels quels.

- carburant pas cher
- prix essence
- prix gazole
- station service
- pompe essence
- économie carburant
- comparateur prix carburant
- où faire le plein pas cher
- E85 pas cher
- SP95 prix

---

## Icône — Résumé exigences

| Paramètre | Valeur requise |
|-----------|---------------|
| Format | PNG 32 bits |
| Dimensions | 512 × 512 px |
| Fond transparent | Non (fond plein requis) |
| Source du projet | `mipmap-*/ic_launcher*.xml` → exporter via Android Studio |

### Export depuis Android Studio :
1. Clic droit sur `res/mipmap-anydpi/ic_launcher.xml`
2. → *Show in Explorer*
3. Ou utiliser **Image Asset Studio** (`File > New > Image Asset`) pour générer un PNG 512×512 propre.

---

## Feature Graphic (bannière)

| Paramètre | Valeur requise |
|-----------|---------------|
| Format | JPG ou PNG 24-bit |
| Dimensions | 1024 × 500 px |
| Fond transparent | Non |

**Proposition de contenu :**
- Fond : couleur primaire de l'app (`#primaryContainer` ou dégradé)
- Gauche : icône pompe à essence (grande)
- Centre ou droite : texte blanc bold "CarbuCheap" + sous-titre "Le plein au meilleur prix"

---

## Captures d'écran — Guide

Format requis par Google Play : **portrait 9:16**, min 1080 × 1920 px.  
Recommandé : **1080 × 2400 px** (Pixel 7/8).

### 5 captures à réaliser dans l'ordre :

| # | Nom du fichier suggéré | Contenu à capturer |
|---|----------------------|-------------------|
| 1 | `screenshot_01_splash.png` | Splash screen avec icône pompe et "Localisation en cours…" |
| 2 | `screenshot_02_loading.png` | Dashboard — état chargement (spinner "Recherche des stations…") |
| 3 | `screenshot_03_list_gazole.png` | Liste complète Gazole — 5+ stations visibles, 1ère carte mise en avant |
| 4 | `screenshot_04_filter_sp95.png` | Filtre SP95 actif — chip sélectionné visible, résultats SP95 |
| 5 | `screenshot_05_radius_20km.png` | Filtre rayon 20 km actif — résultats élargis visibles |

### Procédure Android Studio :

1. **Lancer l'émulateur** : AVD Manager → Pixel 7 ou Pixel 8 (API 33 minimum)
2. **Simuler la position GPS** :
   - Dans l'émulateur : icône `...` (Extended Controls) → onglet **Location**
   - Latitude : `48.856600`  Longitude : `2.352200`  (Paris, centre)
   - Cliquer **Set Location**
3. **Lancer l'app** depuis Android Studio (bouton Run ▶)
4. **Capturer** : icône appareil photo dans la barre latérale de l'émulateur  
   → Les fichiers PNG s'enregistrent dans le dossier de votre choix
5. **Vérifier les dimensions** : l'émulateur Pixel 7 produit du 1080×2400 nativement

### Conseil pour la capture n°1 (Splash)
Le splash Permission Dialog apparaît au premier lancement.  
Si le dialog ne s'affiche plus (permissions déjà accordées) : désinstaller l'app dans l'émulateur, puis relancer.

---

## Politique de confidentialité — URL requise

Le Play Store exige une URL publique vers la politique de confidentialité car l'app accède à la **localisation** (`ACCESS_FINE_LOCATION`).

Voir fichier : [`privacy-policy.html`](./privacy-policy.html)

Options d'hébergement gratuites :
- **GitHub Pages** : `https://<username>.github.io/carbucheap-privacy/`
- **Notion** : publier la page et copier l'URL publique
- **Google Sites** : créer une page statique gratuite

---

## Checklist finale avant soumission

- [ ] AAB signé généré (`Build > Generate Signed Bundle/APK > Android App Bundle`)
- [ ] Keystore de production créée et sauvegardée en lieu sûr
- [ ] Icône 512×512 PNG uploadée
- [ ] Feature Graphic 1024×500 uploadée
- [ ] 5 captures d'écran uploadées (format portrait 1080×2400)
- [ ] URL politique de confidentialité publique renseignée
- [ ] Questionnaire IARC (Content Rating) complété → "Tout public"
- [ ] Pays de distribution : France (ou monde entier)
- [ ] Prix : Gratuit
- [ ] `versionCode = 1`, `versionName = "1.0"` dans `app/build.gradle.kts` ✓
