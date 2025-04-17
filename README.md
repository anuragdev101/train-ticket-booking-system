# Train Booking System

A simple REST API for managing train tickets between London and France.

## Features

- Purchase train tickets
- View ticket details
- View passengers by section
- Remove passengers from train
- Modify passenger seats

## API Endpoints

### Purchase a Ticket
```http
POST /api/tickets
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
}
```

### Get Ticket Details
```http
GET /api/tickets/{ticketId}
```

### Get Passengers by Section
```http
GET /api/tickets/section/{section}
```
Where section is either "A" or "B"

### Remove a Passenger
```http
DELETE /api/tickets/{ticketId}
```

### Modify Passenger Seat
```http
PUT /api/tickets/{ticketId}/seat?section={section}&seatNumber={seatNumber}
```
Where:
- section is either "A" or "B"
- seatNumber is a positive integer

## Building and Running

1. Make sure you have Java 17 and Maven installed
2. Clone the repository
3. Run the following command:
```bash
mvn spring-boot:run
```
4. The application will start on http://localhost:8080

## Technical Details

- Built with Spring Boot 3.2.3
- Uses in-memory storage (no database required)
- Each section (A and B) has 50 seats
- Ticket price is fixed at $20
- All endpoints return appropriate HTTP status codes
- Input validation is implemented for all endpoints 