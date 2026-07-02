# Merjane Inventory API (Refactor)

## Description du projet
Application Spring Boot de gestion d'inventaire et de traitement de commandes.

Le domaine met en oeuvre 3 types de produits:
- NORMAL: gestion classique du stock, notification de delai en rupture.
- SEASONAL: vente en fenetre saisonniere, indisponibilite si delai hors saison.
- EXPIRABLE: vente interdite apres date d'expiration.

## Architecture retenue
Structure en couches:
- Controller: orchestration HTTP (`/orders/{id}/processOrder`).
- Service applicatif: coordination du traitement d'une commande.
- Regles metier: strategies de traitement par type de produit.
- Repository: acces aux entites JPA (`Order`, `Product`).

Le refactoring a introduit un decoupage explicite des regles metier par type via le pattern Strategy:
- `ProductProcessingStrategy`
- `NormalProductProcessingStrategy`
- `SeasonalProductProcessingStrategy`
- `ExpirableProductProcessingStrategy`

## Choix de conception et de refactoring
- Separation of Concerns: suppression de la logique metier du controller.
- SRP: une strategie par type produit.
- OCP: ajout d'un nouveau type possible sans modifier le controller.
- DIP: le controller depend d'un service applicatif, pas des details metier.
- DRY: factorisation des comportements repetes de traitement.

Corrections techniques complementaires (sans impact fonctionnel):
- Alignement des repositories avec des identifiants `Long`.
- Passage du compilateur Maven sur `release` Java.
- Mise a jour Lombok pour compatibilite JDK recentes.

## Patterns utilises
- Strategy: encapsulation des regles metier par type produit.

## Prerequis
- JDK 17 recommande.
- Maven Wrapper (`./mvnw`).

## Installation
Depuis la racine du projet:

```bash
cd api
chmod +x mvnw
./mvnw clean compile
```

## Commandes build et tests
Depuis `api`:

```bash
# Compilation
./mvnw clean compile

# Tests unitaires
./mvnw test

# Tests d'integration
./mvnw integration-test

# Verification complete
./mvnw verify
```

Notes environnement local (JDK 25):
- Si JaCoCo n'est pas compatible localement, utiliser temporairement:

```bash
./mvnw -Djacoco.skip=true clean test
./mvnw -Djacoco.skip=true clean integration-test
```

## Couverture
- Seuil configure dans le projet: 80% (JaCoCo check).
- Pourcentage exact non calcule dans cette execution locale car JaCoCo peut etre desactive selon la version JDK.

## Ameliorations apportees
- Controller simplifie et plus lisible.
- Regles metier isolees dans des strategies testables independamment.
- Ajout de tests unitaires metier couvrant les cas critiques:
  - NORMAL: disponible, rupture + lead time.
  - SEASONAL: disponible, rupture, delai dans saison, delai hors saison, notification indisponibilite.
  - EXPIRABLE: valide, expire, vente impossible apres expiration.
  - Cas negatifs: type inconnu, type null.

## Hypotheses
- Les classes marquees `// WARN: Should not be changed during the exercise` restent inchangées.
- Les regles metier existantes sont conservees a l'identique.

## Pistes d'amelioration futures
- Introduire un vrai modele de domaine pour remplacer les types string (enum + validation).
- Ajouter des tests d'architecture (ArchUnit) pour proteger les dependances entre couches.
- Ajouter des tests de contrat API (schema + payloads).
- Ameliorer la gestion des erreurs metier HTTP (codes explicites, payload erreur standard).

## Rappel consignes exercice
- Ignorer les migrations BDD.
- Ne pas modifier les classes qui ont un commentaire `// WARN: Should not be changed during the exercise`.