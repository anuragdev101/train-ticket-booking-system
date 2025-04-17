package com.train.controller;

import com.train.model.Ticket;
import com.train.dto.TicketRequest;
import com.train.service.TrainBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")  // Enable CORS for testing
public class TrainBookingController {
    private final TrainBookingService trainBookingService;

    public TrainBookingController(TrainBookingService trainBookingService) {
        this.trainBookingService = trainBookingService;
    }

    @GetMapping("/stations")
    public ResponseEntity<Object> getAllStations() {
        try {
            Set<String> stations = trainBookingService.getAllStations();
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve stations",
                "message", e.getMessage(),
                "type", "INTERNAL_ERROR"
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Object> purchaseTicket(@Valid @RequestBody TicketRequest request) {
        try {
            Ticket ticket = trainBookingService.purchaseTicket(request);
            return ResponseEntity.ok(ticket);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already has a ticket")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", HttpStatus.CONFLICT.value());
                response.put("error", "Duplicate Ticket");
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Operation Failed",
                "message", e.getMessage(),
                "type", "OPERATION_FAILED"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid Input",
                "message", e.getMessage(),
                "type", "INVALID_INPUT"
            ));
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Object> getTicket(@PathVariable String ticketId) {
        try {
            Ticket ticket = trainBookingService.getTicket(ticketId);
            if (ticket == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Ticket Not Found",
                    "message", "No ticket found with ID: " + ticketId,
                    "type", "NOT_FOUND"
                ));
            }
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid Input",
                "message", e.getMessage(),
                "type", "INVALID_INPUT"
            ));
        }
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<Object> getTicketsBySection(@PathVariable String section) {
        try {
            List<Ticket> tickets = trainBookingService.getTicketsBySection(section);
            return ResponseEntity.ok(Map.of(
                "section", section,
                "tickets", tickets,
                "count", tickets.size()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid Section",
                "message", e.getMessage(),
                "type", "INVALID_INPUT"
            ));
        }
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Object> removeTicket(@PathVariable String ticketId) {
        try {
            trainBookingService.removeTicket(ticketId);
            return ResponseEntity.ok(Map.of(
                "message", "Ticket successfully removed",
                "ticketId", ticketId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid Input",
                "message", e.getMessage(),
                "type", "INVALID_INPUT"
            ));
        }
    }

    @PutMapping("/{ticketId}/seat")
    public ResponseEntity<Object> modifySeat(
            @PathVariable String ticketId,
            @RequestParam String section,
            @RequestParam int seatNumber) {
        try {
            Ticket ticket = trainBookingService.modifySeat(ticketId, section, seatNumber);
            if (ticket == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Ticket Not Found",
                    "message", "No ticket found with ID: " + ticketId,
                    "type", "NOT_FOUND"
                ));
            }
            return ResponseEntity.ok(Map.of(
                "message", "Seat modified successfully",
                "ticket", ticket
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid Input",
                "message", e.getMessage(),
                "type", "INVALID_INPUT"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Operation Failed",
                "message", e.getMessage(),
                "type", "OPERATION_FAILED"
            ));
        }
    }
} 