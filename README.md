### Consignes: 
* Ignorez les migrations BDD
* Ne pas modifier les classes qui ont un commentaire: `// WARN: Should not be changed during the exercise
`
* Pour lancer les tests (depuis le sous-répertoire `api`) :
  * unitaires: `mvnw test`
  * integration: `mvnw integration-test`
  * tous: `mvnw verify`
---

## Refactoring notes

### Structure

```
controllers/          OrderController + the advice mapping domain errors to statuses
services/             OrderProcessingService: loads the order, dispatches its items
services/products/    one processor per product family, behind ProductProcessor
entities/             Product, its ProductType and the few rules that belong to it
```

The controller used to hold the type dispatch, the stock updates and the
repository calls. It now only reads the path variable and builds the response.
Each product family owns its rules in its own class, so a fourth family is a new
file rather than another branch in an `if/else` chain.

`Order`, `NotificationService` and `ProcessOrderResponse` were left untouched as
requested.

### Deliberate behaviour changes

Everything else is behaviour-preserving; these four are on purpose:

1. **Unknown order returns 404 instead of 500.** `findById().get()` threw
   `NoSuchElementException` on a wrong id, which Spring reported as a server
   error for what is a client mistake.
2. **Order processing is transactional.** A failure halfway through the basket
   used to leave the first products decremented and the rest untouched.
3. **`Product.type` is an enum instead of a `String`.** Trade-off worth knowing:
   a row holding an unknown type used to be skipped silently, it now fails on
   load. Given the three families are fixed by the spec, failing loudly beats
   silently not decrementing a stock.
4. **Saving now always happens before notifying.** The two orders were mixed
   depending on the branch; neither is observable by a client.

### Behaviours that look like bugs and were kept

Both are covered by a test so they stay visible:

- A seasonal product **in stock but out of season** gets an out-of-stock
  notification and keeps its stock.
- An expirable product that is **fresh but out of stock** is written off and
  gets an expiration notification.

### Tests

`./mvnw verify` runs 23 tests. Line coverage went from 80.7% to 93.3% and branch
coverage from 70% to 100%.

The integration test was written against the original code and never changed
afterwards: it drives the real endpoint with a basket holding one product per
business case and asserts the stock left in database plus the notifications
sent. Staying green through the whole refactoring is what shows there is no
regression. The unit tests cover the edge cases on top of it, including the
season and expiry boundary days, which are exact thanks to the injected `Clock`.

### Running the application locally

`ddl-auto` was changed from `validate` to `update` in
`api/src/main/resources/application.yml`, otherwise the application cannot
start at all.

`validate` only checks that the schema matches the entities, it never creates
anything. It made sense when the project ran on a dockerised Postgres with a
persistent volume, where the schema was created once and survived restarts. The
datasource is now `jdbc:h2:mem:testdb`, which is empty on every boot, and the
Liquibase changelog is an empty template, so nothing creates the tables and
startup fails on the first missing one. `update` lets Hibernate build the schema
from the entities.

The tests were never affected: `src/test/resources/application.yml` already
overrode `ddl-auto` to `update`.
