**PocketLedger API Contract**

This document defines the API between our Next.js frontend and Spring Boot backend. We want to agree on what the frontend sends and what the backend returns so both sides can be developed at the same time. This is still a draft and may change as we work more on the project.

**Base URL:** For local development, the backend runs at `http://localhost:8080/api`. The Next.js frontend runs at `http://localhost:3000`.

**Money:** Money values are sent as strings. The backend uses `BigDecimal`, and the frontend should use a decimal library when doing calculations with money.
```json
{
  "amount": "-18.47"
}
```

**Sign convention:** Expenses are negative and income is positive. For example: coffee purchase `-6.75`, paycheck `1200.00`, refund = positive amount. CSV imports should be converted to this format.

**Dates:** Dates use `YYYY-MM-DD`. Months use `YYYY-MM`.

**Currency:** Currencies use three-letter uppercase codes, for example: `USD`, `EUR`, `CNY`.

**Authentication:** Protected endpoints will use a login cookie set by the backend:
```text
Cookie: token=<token>
```
The backend identifies the user from the token. The frontend does not send a `userId` with normal requests. The frontend sends requests with `credentials: "include"` so the browser includes the cookie.

**Errors:** Errors use this format:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Amount must not be zero."
  }
}
```
Common HTTP status codes: `400` - Invalid request, `401` - Not logged in or invalid token, `404` - Resource not found, `409` - Conflict such as deleting the `Other` category, `500` - Server error.

**Authentication endpoints**

We use OAuth for login. The backend stores the JSON Web Token (JWT) in an HttpOnly cookie.

**Login:** `GET /oauth2/authorization/{provider}`

Example: `GET /oauth2/authorization/google`

This login works through a browser redirect, so there is no JSON request body. Spring Security uses this path by default, so it is not under `/api`. After login, the backend creates a new user if needed, sets the login cookie, and sends the user back to the frontend.

**Logout:** `POST /api/auth/logout`

Response: `204 No Content`

The backend clears the login cookie.

**Get Current User:** `GET /api/auth/me`

Response: `200 OK`
```json
{
  "user": {
    "id": 1,
    "email": "student@okstate.edu",
    "homeCurrency": "USD"
  }
}
```

The frontend can use this endpoint to check whether the current login is still valid. `homeCurrency` is `USD` by default for a new user.

**Update Home Currency:** `PATCH /api/auth/me`

Request:
```json
{
  "homeCurrency": "EUR"
}
```

Response: `200 OK` and the updated user. The settings page uses this endpoint when the user changes their home currency. The backend then updates `convertedAmount` for existing transactions using the exchange rate from each transaction date.

**Delete Account:** `DELETE /api/auth/me`

Response: `204 No Content`

The user and all of their data are permanently deleted.

**Transaction**

Transaction format:
```json
{
  "id": 42,
  "date": "2026-09-10",
  "description": "Coffee",
  "amount": "-6.75",
  "currency": "USD",
  "convertedAmount": "-6.75",
  "exchangeRate": "1.0000",
  "exchangeRateDate": "2026-09-10",
  "categoryId": 3,
  "accountId": 1,
  "categorySource": "ai"
}
```
Fields: `amount` = original amount, `currency` = original currency, `convertedAmount` = amount in the user's home currency, `exchangeRate` = rate used, `exchangeRateDate` = date of the rate, `categoryId` = assigned category, `accountId` = account for the transaction, `categorySource` = how the category was assigned.

Possible `categorySource` values: `user`, `rule`, `ai`, `default`. The frontend can mark `ai` categories so the user knows they were assigned automatically. `default` means the transaction was put in `Other` because no category was chosen.

**Get Transactions:** `GET /api/transactions`

Optional query parameters: `from`, `to`, `categoryId`, `accountId`

Example: `GET /api/transactions?from=2026-09-01&to=2026-09-30&categoryId=3`

Response: `200 OK`
```json
{
  "transactions": [
    {
      "id": 42,
      "date": "2026-09-10",
      "description": "Coffee",
      "amount": "-6.75",
      "currency": "USD",
      "convertedAmount": "-6.75",
      "exchangeRate": "1.0000",
      "exchangeRateDate": "2026-09-10",
      "categoryId": 3,
      "accountId": 1,
      "categorySource": "ai"
    }
  ]
}
```

**Create Transaction:** `POST /api/transactions`

Request:
```json
{
  "date": "2026-09-10",
  "description": "Coffee",
  "amount": "-6.75",
  "currency": "EUR",
  "accountId": 1,
  "categoryId": 3
}
```
`categoryId` is optional. For manual transactions the user chooses the category, so `categorySource` is `user`. If no category is provided, the backend uses `Other` and `categorySource` is `default`. If the transaction currency is different from the user's home currency, the backend gets the exchange rate for the transaction date and stores the converted amount.

Response: `201 Created` and the created transaction.

**Update Transaction:** `PUT /api/transactions/{id}`

Example: `PUT /api/transactions/42`

Request: Same fields as `POST /api/transactions`. If the user manually changes the category, `categorySource` becomes `user`.

Response: `200 OK` and the updated transaction.

**Delete Transaction:** `DELETE /api/transactions/{id}`

Response: `204 No Content`

Transactions use hard delete, so deleted transactions are permanently removed from the database and cannot be restored.

**Categories**

For the MVP, categories support read and delete. Users cannot create or rename categories. Every new user starts with a default list of categories.

**Get Categories:** `GET /api/categories`

Response: `200 OK`
```json
{
  "categories": [
    {
      "id": 1,
      "name": "Food",
      "color": "#E8833A"
    },
    {
      "id": 2,
      "name": "Transport",
      "color": "#4A90D9"
    },
    {
      "id": 8,
      "name": "Other",
      "color": "#9B9B9B"
    }
  ]
}
```

**Delete Category:** `DELETE /api/categories/{id}`

Example: `DELETE /api/categories/2`

Response: `204 No Content`

All transactions in the deleted category are moved to `Other` and their `categorySource` becomes `default`. Budgets for the deleted category are removed. The `Other` category cannot be deleted (`409`).

**Accounts**

For the MVP, accounts support read and create.

**Get Accounts:** `GET /api/accounts`

Response: `200 OK`
```json
{
  "accounts": [
    {
      "id": 1,
      "name": "Checking",
      "type": "checking",
      "currency": "USD"
    },
    {
      "id": 2,
      "name": "Cash",
      "type": "cash",
      "currency": "USD"
    }
  ]
}
```
Possible account types: `checking`, `savings`, `credit`, `cash`.

**Create Account:** `POST /api/accounts`

Request:
```json
{
  "name": "Travel Card",
  "type": "credit",
  "currency": "EUR"
}
```

Response: `201 Created` and the created account.

**Budgets**

**Get Monthly Budget:** `GET /api/budgets?month=2026-09`

Response: `200 OK`
```json
{
  "month": "2026-09",
  "totalPlanned": "1450.00",
  "totalSpent": "1187.32",
  "categories": [
    {
      "categoryId": 1,
      "categoryName": "Food",
      "planned": "400.00",
      "spent": "312.18",
      "remaining": "87.82"
    }
  ]
}
```

Budget values are positive. `spent` is the amount spent in that category for the month. `remaining` can be negative if the user goes over budget.

**Save Monthly Budget:** `PUT /api/budgets`

Request:
```json
{
  "month": "2026-09",
  "budgets": [
    {
      "categoryId": 1,
      "amount": "400.00"
    },
    {
      "categoryId": 2,
      "amount": "80.00"
    }
  ]
}
```

Response: `200 OK` with the same format as `GET /api/budgets`.

**CSV Import**

CSV import has two steps: upload the file and check the column mapping, then confirm the mapping and import the transactions.

**Preview CSV:** `POST /api/import/preview`

Request type: `multipart/form-data`

File field: `file`

Response: `200 OK`
```json
{
  "uploadId": "abc123",
  "columns": [
    "Transaction Date",
    "Description",
    "Amount"
  ],
  "sampleRows": [
    ["08/14/2026", "TARGET", "-6.75"],
    ["08/15/2026", "WALMART #2841", "-52.30"]
  ],
  "rowCount": 284
}
```

The frontend uses this information to let the user choose the correct date, description, and amount columns.

**Import CSV:** `POST /api/import/commit`

Request:
```json
{
  "uploadId": "abc123",
  "accountId": 1,
  "mapping": {
    "date": "Transaction Date",
    "description": "Description",
    "amount": "Amount"
  },
  "dateFormat": "MM/DD/YYYY",
  "signConvention": "negative_is_expense"
}
```

`signConvention` tells the backend how the CSV represents expenses and income.

Response: `200 OK`
```json
{
  "imported": 271,
  "skipped": 11,
  "failed": 2
}
```

`skipped` includes duplicate transactions. Categorization also runs during import. The backend checks saved rules first, then uses AI if no rule matches. If AI also fails, the transaction goes to `Other`.

**AI**

**Categorize Transactions:** `POST /api/ai/categorize`

This endpoint can be used to re-categorize existing transactions.

Request:
```json
{
  "transactionIds": [42, 57, 61]
}
```

Response: `200 OK`
```json
{
  "results": [
    {
      "id": 42,
      "categoryId": 1,
      "source": "rule",
      "confidence": null
    },
    {
      "id": 57,
      "categoryId": 3,
      "source": "ai",
      "confidence": 0.92
    },
    {
      "id": 61,
      "categoryId": 8,
      "source": "default",
      "confidence": 0.31
    }
  ]
}
```
The backend only accepts categories that already belong to the user. If no category can be assigned, `categoryId` is the user's `Other` category and `source` is `default`. If Gemini is unavailable, transactions that cannot be handled by saved rules are also returned as `Other` with `default`.

**Ask about spending:** `POST /api/ai/ask`

Request:
```json
{
  "question": "How much did I spend on food in October?"
}
```

Response: `200 OK`
```json
{
  "answer": "You spent $312.18 on Food in October 2026.",
  "transactionIds": [42, 57, 61]
}
```

`transactionIds` contains the transactions used for the answer so the frontend can display them.

**Exchange rate**

**Get exchange rate:** `GET /api/fx/rate?from=EUR&to=USD&date=2026-09-10`

Response: `200 OK`
```json
{
  "from": "EUR",
  "to": "USD",
  "date": "2026-09-10",
  "rate": "1.0824"
}
```

Currency conversion is handled by the backend. The backend uses the exchange rate from the transaction date instead of the current exchange rate.

**Assumptions**
1. We use OAuth for login. This contract assumes the backend stores the JWT in an HttpOnly cookie. We may change this later. If we use `localStorage` instead, the frontend will send `Authorization: Bearer <token>`, and the backend will need to return the token after login.
2. New users start with `USD` as their `homeCurrency`. They can change it in settings using `PATCH /api/auth/me`. If it changes, the backend recalculates `convertedAmount` for existing transactions so the app uses the new home currency.
3. `categorySource` `default` means the category did not come from the user, a saved rule, or AI. This can happen when a manual transaction has no category, when rules and AI both fail during CSV import or `POST /api/ai/categorize`, or when a category is deleted. In these cases, the transaction goes to `Other`. We still need to decide what AI confidence score is too low.
4. Rules and AI are only used for CSV imports and `POST /api/ai/categorize`. Manual transactions use the category selected by the user. We have not decided yet how new rules will be created.
5. `Other` is one of the default categories and cannot be deleted. The frontend should not show a delete button for it. We have not decided the full default category list yet.
6. All deletes are hard deletes for now, including transactions, categories, and user accounts.