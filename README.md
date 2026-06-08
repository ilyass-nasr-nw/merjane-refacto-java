### Consignes: 
* Ignorez les migrations BDD
* Ne pas modifier les classes qui ont un commentaire: `// WARN: Should not be changed during the exercise
`
* Pour lancer les tests (depuis le sous-répertoire `api`) :
  * unitaires: `mvnw test`
  * integration: `mvnw integration-test`
  * tous: `mvnw verify`

#### 🛠 Refactoring Approach & Architecture

Afin de répondre aux exigences de propreté du code, de maintenabilité et d'absence de régression, le refactoring a été mené en deux phases :

**1. Sécurité (Golden Master Testing)**
Avant toute modification, une suite de tests d'intégration (Characterization Tests) a été mise en place pour capturer le comportement exact du code legacy. Cela garantit le critère de **zéro régression**.

**2. Architecture & Design Patterns**
Le "Fat Controller" et les blocs `if/else` imbriqués ont été remplacés par une architecture orientée domaine :
* **Strategy Pattern :** Création d'une interface `ProductProcessor` et de ses implémentations concrètes (`NormalProductProcessor`, `SeasonalProductProcessor`, `ExpirableProductProcessor`).
* **Single Responsibility Principle (SRP) :** Chaque classe métier est désormais responsable d'un seul type de produit.
* **Open/Closed Principle (OCP) :** L'ajout d'un futur type de produit (ex: "DIGITAL") se fera par l'ajout d'une nouvelle classe, sans modifier le code existant via l'utilisation de la `ProductProcessorFactory`.
* **Séparation des couches :** La logique a été extraite du contrôleur vers un `OrderProcessingService` dédié, rendant l'API purement responsable du routage HTTP.