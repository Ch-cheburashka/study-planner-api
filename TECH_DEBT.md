# Technical Debt

## Tests

- Current tests cover mainly service logic.
- No controller tests yet.
- No integration tests with real Spring context + HTTP layer.
- No repository tests for search query.
- Need tests for validation errors.

## Search

- Need to handle empty or blank keyword.

## Validation / Error handling

- Need consistent handling of null vs empty string.
- Need clearer validation messages.
- GlobalExceptionHandler exists, but error response structure can be improved.
- Field name `fieldErrors` is not ideal because the object may contain general errors too.
- Need consistent format for validation errors, not found errors and invalid JSON.

## HTTP semantics
- Need to decide whether PUT should be full update or whether PATCH should be added later.

## API documentation

- Consider adding OpenAPI/Swagger after the API contract stabilizes.