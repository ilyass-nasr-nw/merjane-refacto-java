# **Énoncé**

Bonjour et bienvenue dans l'équipe de Merjane, le leader de la vente en ligne à Aïn Sebaâ.

Chez Merjane, nous faisons de notre mieux chaque jour pour fournir à nos clients les produits qu'ils aiment et leur assurer une satisfaction maximale. C'est pour cela que nous gardons un œil attentif sur la disponibilité et la qualité de nos produits.

Malheureusement, certains produits ont des particularités liées à leur disponibilité ou à leur expiration. Et c'est là que vous intervenez.

Notre équipe informatique dont vous faites partie a mis en place un système pour suivre notre inventaire. Il a été développé par Hamid, une personne pleine de bon sens qui est partie pour de nouvelles aventures.

Votre objectif est d'ajouter un nouveau type de produit à notre catalogue, un produit "FLASHSALE", et de gérer sa disponibilité de manière adéquate.

Mais d'abord, laissez-moi vous présenter notre système :

- Tous les produits ont une valeur **`available`** qui désigne le nombre d'unités disponibles en stock.
- Tous les produits ont une valeur **`leadTime`** qui désigne le nombre de jours nécessaires pour le réapprovisionnement.
- À la fin de chaque commande, notre système décrémente la valeur **`available`** pour chaque produit commandé.

Jusqu'ici, tout va bien. Mais voici où ça se corse :

- Les produits "NORMAL" ne présentent aucune particularité. Lorsqu'ils sont en rupture de stock, un délai est simplement annoncé aux clients.
- Les produits "SEASONAL" ne sont disponibles qu'à certaines périodes de l'année. Lorsqu'ils sont en rupture de stock, un délai est annoncé aux clients, mais si ce délai dépasse la saison de disponibilité, le produit est considéré comme non disponible. Quand le produit est considéré comme non disponible, les client sont notifiés de cette indisponibilité. 
- Les produits "EXPIRABLE" ont une date d'expiration. Ils peuvent être vendus normalement tant qu'ils n'ont pas expiré, mais ne sont plus disponibles une fois la date d'expiration passée.
- Les produits "FLASHSALE" sont des produits en vente flash. Ils sont disponibles pour une période de temps très limitée et ont une quantité maximale d'articles pouvant être vendus. Une fois la période de vente flash terminée ou le nombre maximal d'articles vendus, ils ne sont plus disponibles.

Votre tâche est d'ajouter le nouveau type de produits "FLASHSALE" à notre système, en prenant en compte les particularités de sa disponibilité. Vous devrez également faire en sorte que l'ensemble du code soit facilement compréhensible et maintenable pour le prochain développeur qui travaillera sur ce projet.


### Consignes: 
* Ignorez les migrations BDD
* Ne pas modifier les classes qui ont un commentaire: `// WARN: Should not be changed during the exercise
`
* Pour lancer les tests (depuis le sous-répertoire `api`) :
  * unitaires: `mvnw test`
  * integration: `mvnw integration-test`
  * tous: `mvnw verify`