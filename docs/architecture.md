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
- OpenAPI / Swagger
- springdoc-openapi
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

## Tenant and Account Model

### User

A `User` represents a real human identity inside the system.

Users authenticate globally using JWT authentication.

Global system roles are stored separately in `user_roles`.

Examples:

- USER
- ADMIN

These roles are system-wide roles.

---

### Account

An `Account` represents a tenant, company, workspace, or organization.

Billing entities such as:

- subscriptions
- invoices
- usage
- entitlements

will eventually belong to accounts rather than directly to users.

---

### Account Membership

Users are connected to accounts through `account_members`.

This allows the same user to belong to multiple accounts.

Example:

- Igor → OWNER → Company A
- Igor → MEMBER → Company B

---

### Account Roles

Account roles are tenant-scoped roles.

Planned account roles:

- OWNER
- ADMIN
- MEMBER
- BILLING_MANAGER

These roles are different from global system roles.

Example:

A user may be:

- global USER
- account OWNER inside one tenant
- account MEMBER inside another tenant

---

### Architectural Direction

The platform is moving toward a multi-tenant architecture.

Future authorization decisions will likely depend on:

- authenticated user
- current account
- account membership
- account role

---

### Registration and Account Creation Strategy

The platform follows a hybrid onboarding model.

#### Self-registration

When a user registers normally:

1. a new user is created
2. a default personal account/workspace is created
3. the user becomes the OWNER of that account

Goal:

Provide frictionless onboarding and immediately usable workspace experience.

---

#### Invitation-based registration

In the future, users may also join existing accounts through invitation flows.

Planned flow:

1. account OWNER or ADMIN creates invitation
2. invited user registers or logs in
3. membership is added to existing account

In invitation-based flows, automatic personal account creation may be skipped.

---

#### Architectural Reasoning

This hybrid model combines:

- simple SaaS onboarding
- multi-tenant flexibility
- enterprise-style account membership support

while avoiding unnecessary onboarding friction.

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
```

Possible response:

```json
{
  "allowed": true,
  "remaining": 120
}
```

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

## 8. Near-Term Roadmap

### Current Focus

The current focus is transitioning from a generic CRUD backend into a tenant-aware billing and entitlement platform.

### Planned Next Steps

#### 1. Tenant / Account Architecture

Redesign the current `accounts` entity into a tenant/workspace/company model.

Introduce:

- `account_members`
- account-scoped roles
- account ownership model

Goal:

Support multi-tenant authorization and future subscription ownership.

#### 2. Operational Improvements

Planned operational improvements:

- improve README
- improve OpenAPI documentation
- GitHub Actions CI pipeline
- test environment improvements
- possibly Testcontainers later

#### 3. Billing Domain

Planned billing domain entities:

- plans
- features
- subscriptions
- usage events
- entitlements

#### 4. Long-Term Direction

The project should evolve into a lightweight billing and entitlement backend platform rather than a payment gateway
itself.