# Feature Specification: Hotel Booking Membership Discounts & Cancellation Policy

**Feature Branch**: `[001-hotel-booking-application]`  
**Created**: 2025-10-07  
**Status**: Draft  
**Input**: User description: "Hotel booking application that supports discount for membership and also cancellation policy. Membership system with classic, gold, diamond; discounts 0/10/20%. Cancellation policy: 100% refund >48h, 50% refund 24-48h, no refund <24h. Endpoints to calculate refund/penalty."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Membership Discount Applied to Booking (Priority: P1)

As a hotel member, I want my membership discount (classic/gold/diamond) automatically applied to my booking so that I see the correct total and invoice.

**Why this priority**: Ensures members receive the correct benefit and transparency in pricing, which is core to user trust and value.

**Independent Test**: Can be fully tested by creating bookings as users with different membership levels and verifying the discount is correctly reflected in the booking summary and invoice.

**Acceptance Scenarios**:

1. **Given** a user with classic membership, **When** they book a room, **Then** the booking total shows 0% discount.
2. **Given** a user with gold membership, **When** they book a room, **Then** the booking total shows a 10% discount.
3. **Given** a user with diamond membership, **When** they book a room, **Then** the booking total shows a 20% discount.
4. **Given** any member, **When** they view their invoice, **Then** the discount is clearly itemized.

---

### User Story 2 - Booking Cancellation with Refund Policy (Priority: P2)

As a customer, I want to cancel my booking and see the refund or penalty amount based on when I cancel, so I understand the financial impact.

**Why this priority**: Enables users to manage their bookings flexibly and builds trust by making refund rules transparent.

**Independent Test**: Can be fully tested by cancelling bookings at different times before check-in and verifying the refund/penalty is calculated and displayed correctly.

**Acceptance Scenarios**:

1. **Given** a booking cancelled more than 48 hours before check-in, **When** the user cancels, **Then** they receive a 100% refund.
2. **Given** a booking cancelled between 24 and 48 hours before check-in, **When** the user cancels, **Then** they receive a 50% refund.
3. **Given** a booking cancelled less than 24 hours before check-in, **When** the user cancels, **Then** they receive no refund (100% penalty).
4. **Given** any cancellation, **When** the user completes the process, **Then** the refund or penalty amount is clearly indicated in the response.

---

### User Story 3 - Calculate Refund/Penalty Before Cancelling (Priority: P3)

As a customer, I want to check the refund or penalty amount before cancelling my booking, so I can make an informed decision.

**Why this priority**: Reduces user anxiety and support requests by making financial outcomes clear before action.

**Independent Test**: Can be fully tested by querying the refund/penalty calculation endpoint for a booking and verifying the returned amount matches the policy.

**Acceptance Scenarios**:

1. **Given** a booking, **When** the user requests a refund/penalty calculation, **Then** the system returns the correct amount based on current time and check-in date.
2. **Given** a booking, **When** the user proceeds to cancel after checking the calculation, **Then** the actual refund/penalty matches the previewed amount.

---

### User Story 4 - View Membership Level and Benefits (Priority: P2)

As a user, I want to view my current membership level and the associated benefits (discounts), so I understand what advantages I have when booking.

**Why this priority**: Increases transparency and encourages users to upgrade or maintain their membership.

**Independent Test**: Can be fully tested by logging in as a user and retrieving membership details and discount rates via a dedicated endpoint or profile view.

**Acceptance Scenarios**:

1. **Given** a logged-in user, **When** they view their profile or membership info, **Then** their current membership level and discount rate are displayed.
2. **Given** a user with any membership, **When** they view benefits, **Then** the discount rates for all levels are shown for comparison.

---

### User Story 5 - Users Can View and Cancel Only Their Own Bookings (Priority: P1)

As a user, I want to view and cancel only the bookings that I own, so that my data is secure and I cannot affect other users' reservations.

**Why this priority**: Ensures privacy and security, and prevents unauthorized actions on other users' bookings.

**Independent Test**: Can be fully tested by attempting to view or cancel bookings as different users and verifying access is restricted to the user's own bookings.

**Acceptance Scenarios**:

1. **Given** a logged-in user, **When** they list their bookings, **Then** only their own bookings are shown.
2. **Given** a logged-in user, **When** they attempt to view or cancel another user's booking, **Then** access is denied.
3. **Given** a logged-in user, **When** they cancel their own booking, **Then** the cancellation is processed as per policy.

---

### User Story 6 - Admin Can Create Users and Set/Update Membership Level (Priority: P2)

As an admin, I want to create new users and set or update their membership level, so I can manage the membership system.

**Why this priority**: Enables administrative control over user onboarding and membership management.

**Independent Test**: Can be fully tested by creating users and updating membership levels as an admin, and verifying changes are reflected in user profiles.

**Acceptance Scenarios**:

1. **Given** an admin, **When** they create a new user, **Then** the user is added to the system with a specified membership level.
2. **Given** an admin, **When** they update a user's membership level, **Then** the new level is saved and visible to the user.
3. **Given** a non-admin user, **When** they attempt to create or update users, **Then** access is denied.

---

### User Story 7 - Admin Can View All Bookings (Priority: P2)

As an admin, I want to view all bookings in the system, so I can monitor activity and assist with support or reporting.

**Why this priority**: Provides operational oversight and enables support and analytics.

**Independent Test**: Can be fully tested by logging in as an admin and retrieving the full list of bookings, and verifying that non-admins cannot access this data.

**Acceptance Scenarios**:

1. **Given** an admin, **When** they request the list of all bookings, **Then** all bookings in the system are returned.
2. **Given** a non-admin user, **When** they attempt to view all bookings, **Then** access is denied.

---

## Functional Requirements

- The system MUST support three membership levels: classic (0% discount), gold (10%), diamond (20%).
- The booking summary and invoice MUST reflect the correct discount for the user's membership level.
- The system MUST allow customers to cancel bookings and apply refund/penalty rules:
  - >48h before check-in: 100% refund
  - 24-48h before check-in: 50% refund
  - <24h before check-in: no refund (100% penalty)
- The system MUST expose endpoints to:
  - Cancel a booking and return the refund/penalty amount
  - Calculate the refund/penalty for a booking without cancelling
- All calculations MUST be accurate and auditable.

## Success Criteria

- 100% of bookings for members reflect the correct discount in summary and invoice.
- 100% of cancellations apply the correct refund/penalty based on policy.
- Users can view refund/penalty before confirming cancellation in 100% of cases.
- All user stories are covered by acceptance tests.
- No user receives an incorrect discount or refund/penalty.

## Key Entities

- User (with membership level)
- Booking
- Cancellation
- Refund/Penalty

## Assumptions

- Only members (classic, gold, diamond) can book rooms. Non-members cannot book.
- Membership level is determined at the time of booking and does not change retroactively.
- All times are calculated in the hotel's local timezone.
- Refunds are processed automatically via payment gateway upon cancellation, with no manual review step.
- Only bookings with a future check-in date can be cancelled.
- Expected scale (bookings, users, concurrency) is currently unknown. System should be designed to allow future scaling and load assumptions to be revisited.
- Users MUST be logged in to perform any booking, cancellation, or refund/penalty calculation action. There are no special permissions or roles; all authenticated members have the same access rights for these actions.
- Required external systems for payment, notifications, or other integrations are currently unknown. Integration requirements must be clarified before implementation planning.
