# Train Booking System

A simple REST API for managing train tickets between various European cities.

## Features

- Get available stations
- Purchase train tickets with dynamic pricing
- View ticket details
- View passengers by section
- Remove passengers from train
- Modify passenger seats
- Duplicate booking prevention
- Comprehensive error handling

## API Endpoints

### 1. Get Available Stations
```http
GET /api/tickets/stations

Success Response (200 OK):
[
    "London",
    "Paris",
    "Brussels",
    "Amsterdam"
]

Error Response (500 Internal Server Error):
{
    "error": "Failed to retrieve stations",
    "message": "Error details here",
    "type": "INTERNAL_ERROR"
}
```

### 2. Purchase a Ticket
```http
POST /api/tickets
Content-Type: application/json

Request Body:
{
    "from": "London",
    "to": "Paris",
    "user": {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com"
    }
}

Success Response (200 OK):
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "from": "London",
    "to": "Paris",
    "price": 20.0,
    "section": "A",
    "seatNumber": 1,
    "user": {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com"
    }
}

Duplicate Booking Error (409 Conflict):
{
    "status": 409,
    "error": "Duplicate Ticket",
    "message": "User already has a ticket for this route"
}

Invalid Route Error (400 Bad Request):
{
    "error": "Invalid Input",
    "message": "No route found from London to Berlin",
    "type": "INVALID_INPUT"
}
```

### 3. Get Ticket Details
```http
GET /api/tickets/{ticketId}

Success Response (200 OK):
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "from": "London",
    "to": "Paris",
    "price": 20.0,
    "section": "A",
    "seatNumber": 1,
    "user": {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com"
    }
}

Not Found Response (404 Not Found):
{
    "error": "Ticket Not Found",
    "message": "No ticket found with ID: invalid-id",
    "type": "NOT_FOUND"
}
```

### 4. Get Tickets by Section
```http
GET /api/tickets/section/{section}
Where section is either "A" or "B"

Success Response (200 OK):
{
    "section": "A",
    "tickets": [
        {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "from": "London",
            "to": "Paris",
            "price": 20.0,
            "section": "A",
            "seatNumber": 1,
            "user": {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com"
            }
        }
    ],
    "count": 1
}

Error Response (400 Bad Request):
{
    "error": "Invalid Section",
    "message": "Invalid section. Must be either A or B",
    "type": "INVALID_INPUT"
}
```

### 5. Remove a Ticket
```http
DELETE /api/tickets/{ticketId}

Success Response (200 OK):
{
    "message": "Ticket successfully removed",
    "ticketId": "550e8400-e29b-41d4-a716-446655440000"
}

Error Response (400 Bad Request):
{
    "error": "Invalid Input",
    "message": "Ticket ID cannot be null or empty",
    "type": "INVALID_INPUT"
}
```

### 6. Modify Passenger Seat
```http
PUT /api/tickets/{ticketId}/seat?section={section}&seatNumber={seatNumber}

Success Response (200 OK):
{
    "message": "Seat modified successfully",
    "ticket": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "from": "London",
        "to": "Paris",
        "price": 20.0,
        "section": "B",
        "seatNumber": 25,
        "user": {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com"
        }
    }
}

Seat Occupied Error (400 Bad Request):
{
    "error": "Operation Failed",
    "message": "Seat is already occupied",
    "type": "OPERATION_FAILED"
}

Not Found Response (404 Not Found):
{
    "error": "Ticket Not Found",
    "message": "No ticket found with ID: invalid-id",
    "type": "NOT_FOUND"
}
```

## Available Routes and Prices
- London ↔ Paris: $20.0
- London ↔ Brussels: $25.0
- London ↔ Amsterdam: $35.0
- Paris ↔ Amsterdam: $30.0
- Paris ↔ Brussels: $18.0
- Brussels ↔ Amsterdam: $15.0

## Error Types
- `INVALID_INPUT`: Validation errors (invalid parameters, routes, etc.)
- `OPERATION_FAILED`: Business logic errors (seat occupied, etc.)
- `NOT_FOUND`: Resource not found
- `INTERNAL_ERROR`: Unexpected server errors
- `DUPLICATE`: Duplicate booking attempts

## Technical Details

- Built with Spring Boot 3.2.3
- Uses Java 17
- In-memory storage (no database required)
- Each section (A and B) has 50 seats
- Automatic seat allocation
- Duplicate booking prevention
- Input validation
- Comprehensive error handling
- RESTful API design
- CORS enabled

## Building and Running

1. Make sure you have Java 17 and Maven installed
2. Clone the repository
3. Run the following command:
```bash
mvn spring-boot:run
```
4. The application will start on http://localhost:8080

## Testing
You can test the APIs using tools like Postman or curl. Make sure to:
1. Set the `Content-Type: application/json` header for POST requests
2. Use valid route combinations
3. Ensure email addresses are properly formatted
4. Use valid section values (A or B)
5. Use valid seat numbers (1-50) 