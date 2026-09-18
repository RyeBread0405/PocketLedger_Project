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

**Authentication:** Protected endpoints will use:
```text
Authorization: Bearer <token>
```
The backend identifies the user from the token. The frontend does not send a `userId` with normal requests.

**Errors:** Errors use this format:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Amount must not be zero."
  }
}
```
Common HTTP status codes: `400` - Invalid request, `401` - Not logged in or invalid token, `404` - Resource not found, `409` - Conflict such as duplicate email, `500` - Server error.

**Authentication endpoints**
The endpoints below assume we use email/password authentication with JSON Web Token (JWT).

**Register:** `POST /api/auth/register`
Request:

```json
{
  "email": "student@okstate.edu",
  "password": "...",
  "homeCurrency": "USD"
}
```

Response: `201 Created`

```json
{
  "user": {
    "id": 1,
    "email": "student@okstate.edu",
    "homeCurrency": "USD"
  },
  "token": "eyJhbGci..."
}
```

**Login:** `POST /api/auth/login`
Request:

```json
{
  "email": "student@okstate.edu",
  "password": "..."
}
```

Response: `200 OK`

```json
{
  "user": {
    "id": 1,
    "email": "student@okstate.edu",
    "homeCurrency": "USD"
  },
  "token": "eyJhbGci..."
}
```

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
The frontend can use this endpoint to check whether the current login is still valid.

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

Possible `categorySource` values: `user`, `rule`, `ai`. The frontend can mark `"ai"` categories so the user knows they were assigned automatically.

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

`categoryId` is optional. If no category is provided, the backend checks saved rules first and then uses AI if no rule matches. If the transaction currency is different from the user's home currency, the backend gets the exchange rate for the transaction date and stores the converted amount.
Response: `201 Created` and the created transaction.

**Update Transaction:** `PUT /api/transactions/{id}`
Example: `PUT /api/transactions/42`
Request: Same fields as `POST /api/transactions`. If the user manually changes the category, `categorySource` becomes `"user"`.
Response: `200 OK` and the updated transaction.

**Delete Transaction:** `DELETE /api/transactions/{id}`
Response: `204 No Content`
Transactions use soft delete, so deleted transactions stay in the database with a `deletedAt` value but do not appear in normal queries.

**Categories**
For the MVP, categories support read and create.

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
    }
  ]
}
```

**Create Category:** `POST /api/categories`
Request:
```json
{
  "name": "Textbooks",
  "color": "#6C5CE7"
}
```

Response: `201 Created` and the created category.

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
`skipped` includes duplicate transactions. Categorization runs during the import process.

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
      "categoryId": null,
      "source": "unresolved",
      "confidence": 0.31
    }
  ]
}
```

The backend only accepts categories that already belong to the user. If no category can be assigned, `categoryId` is `null` and `source` is `"unresolved"`. If Gemini is unavailable, transactions that cannot be handled by saved rules are also returned as unresolved.

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

**Exchage rate**
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

**Questions**
1. Solved: Expenses be negative and income positive, using absolute value when needed.
2. If we use JWT authentication, where should the frontend store the login token?
3. What AI confidence score should be considered too low?
4. Should users be able to rename or delete categories? Should they also be able to rename accounts?
5. Should we check saved rules first and then use Gemini for AI categorization? If Gemini fails or the confidence is too low, should we use "Other" or leave the category empty? How should new rules be created?
6. Should transactions use soft delete so users can undo them? When a user deletes their account, should all of their data be permanently deleted?
7. Should every new user start with a default list of categories and still be able to add more?
