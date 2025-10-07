# Data Model: Hotel Booking Membership Discounts & Cancellation Policy

**Date**: 2025-10-07
**Feature**: /specs/001-hotel-booking-application/spec.md

## Entities

### User
- id: UUID (PK)
- username: String (unique, required)
- password: String (hashed, required)
- email: String (unique, required)
- membershipLevel: Enum [CLASSIC, GOLD, DIAMOND] (required)
- createdAt: Timestamp
- updatedAt: Timestamp

### Booking
- id: UUID (PK)
- userId: UUID (FK to User, required)
- roomId: UUID (FK to Room, required)
- checkIn: DateTime (required)
- checkOut: DateTime (required)
- totalAmount: Decimal (required)
- discountAmount: Decimal (required)
- finalAmount: Decimal (required)
- status: Enum [ACTIVE, CANCELLED] (default: ACTIVE)
- createdAt: Timestamp
- updatedAt: Timestamp

### Cancellation
- id: UUID (PK)
- bookingId: UUID (FK to Booking, required, unique)
- cancelledAt: DateTime (required)
- refundAmount: Decimal (required)
- penaltyAmount: Decimal (required)
- refundStatus: Enum [PENDING, COMPLETED, FAILED] (default: PENDING)

### Refund/Penalty (calculation only, not persisted)
- bookingId: UUID
- refundAmount: Decimal
- penaltyAmount: Decimal
- calculationTime: DateTime
- policyTier: Enum [FULL, PARTIAL, NONE]

### Room (minimal, for reference)
- id: UUID (PK)
- roomNumber: String (unique, required)
- type: String
- pricePerNight: Decimal

## Relationships
- User 1--* Booking
- Booking 1--1 Cancellation (if cancelled)
- Booking *--1 Room

## Validation Rules
- User must have unique username and email.
- Booking dates must be in the future and checkOut > checkIn.
- Only ACTIVE bookings with future check-in can be cancelled.
- Refund/penalty calculation follows the cancellation policy tiers.

## State Transitions
- Booking: ACTIVE → CANCELLED (on cancellation)
- Cancellation: PENDING → COMPLETED/FAILED (on refund processing)

## Notes
- Membership level is fixed at booking time.
- Refunds are processed automatically via payment gateway.
- All times are in hotel local timezone.

