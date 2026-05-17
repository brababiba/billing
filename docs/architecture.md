# Billing Project Architecture

## 1. Project Goal

This project is a learning and experimental backend system for building a simplified billing, subscription, usage
tracking, and entitlement platform.

The goal is not to replace Stripe, but to build a lightweight backend layer that can manage:

- users and authentication
- accounts / tenants
- subscriptions
- plans and features
- usage events
- entitlement checks
- billing-related state
- integrations with external payment providers

External payment systems such as Stripe, PayPal, Paddle, or others may be used later as payment providers.

---

## 2. Current Technical Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT authentication
- PostgreSQL
- Flyway
- Docker Compose
- Maven Wrapper
- JUnit
- MockMvc
- Mockito

---

## 3. Current Implemented Areas

### Authentication and Authorization

Implemented:

- user registration
- password hashing with BCrypt
- login
- JWT generation
- JWT validation
- Bearer token authentication
- SecurityContext integration
- role-based authorization
- `users` table
- `user_roles` table

Current roles:

- `USER`
- `ADMIN`

---

## 4. Current Database Model

### users

Represents a real application user.

Current fields:

- `id`
- `email`
- `password_hash`
- `created_at`

### user_roles

Represents global roles assigned to users.

Current fields:

- `user_id`
- `role`

One user can have multiple roles.

### accounts

Currently this is still a simple CRUD entity.

Important note:

In the future, `accounts` will likely become a tenant / organization / workspace concept and should be connected to
users through an `account_members` table.

---

## 5. Future Domain Model

Expected future entities:

### tenants / accounts

Represents a company, workspace, customer account, or tenant.

### account_members

Connects users to accounts.

Possible fields:

- `account_id`
- `user_id`
- `role`
- `created_at`

Example account-level roles:

- `OWNER`
- `ADMIN`
- `MEMBER`
- `BILLING_MANAGER`

### plans

Represents available billing plans.

Example:

- Free
- Basic
- Pro
- Enterprise

### features

Represents product capabilities.

Example:

- `EXPORT_PDF`
- `AI_TOKENS`
- `API_ACCESS`
- `ADVANCED_REPORTS`

### plan_features

Defines which features are included in each plan.

### subscriptions

Represents active billing state for an account.

Possible statuses:

- `ACTIVE`
- `TRIALING`
- `PAST_DUE`
- `CANCELED`
- `EXPIRED`

### usage_events

Stores raw usage events.

Example:

- API call
- AI token usage
- document export
- storage usage

### usage_aggregates

Stores pre-calculated usage totals per billing period.

### entitlements

Represents what an account/user is allowed to do.

Example API:

```http
GET /api/entitlements/check?feature=EXPORT_PDF
Possible response:

{
  "allowed": true,
  "remaining": 120
}
invoices
Represents generated invoices or invoice-like internal records.

payments
Represents payment attempts and payment results.

payment_providers
Represents external systems such as Stripe, PayPal, Paddle, etc.

webhook_events
Stores external provider webhook events for idempotency and audit.

6. Architectural Principles
Code is the source of truth
The project should remain understandable from:

code structure
package names
database migrations
tests
documentation
Small steps, stable checkpoints
Each meaningful feature should be implemented in small steps and committed after tests pass.

Flyway migrations only move forward
Database changes should be expressed as Flyway migrations.

Avoid premature overengineering
Do not introduce complex abstractions before they solve a real problem.

Avoid hidden business logic
Important billing, security, and entitlement decisions should be visible in services and tests.

Test critical flows
Especially:

authentication
authorization
billing rules
subscriptions
usage limits
payments
webhook idempotency
7. Current Known Architectural Decisions
JWT is used for authentication
JWT is currently implemented directly inside the application for learning and MVP purposes.

Future option:

Replace internal JWT issuing with an external identity provider such as Keycloak, Auth0, Okta, Cognito, or Azure AD.

Roles are stored separately
Roles are stored in user_roles, not as a single column in users.

Reason:

A user may have multiple roles.

PostgreSQL runs in Docker
Local development uses Docker Compose for PostgreSQL.

The Spring Boot application can run either:

locally from IntelliJ
inside Docker
Accounts are not yet connected to users
This is intentional for now.

Future work:

Introduce tenant/account membership model.

8. Near-Term Roadmap
Finish security tests.
Stabilize authentication and authorization.
Add account_members / tenant model.
Redesign current accounts entity if needed.
Introduce plans and features.
Add subscription model.
Add entitlement check API.
Add usage event API.
Add usage aggregation.
Add payment provider abstraction.