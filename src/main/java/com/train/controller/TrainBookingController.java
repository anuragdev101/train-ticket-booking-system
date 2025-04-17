package com.train.controller;

import com.train.model.Ticket;
import com.train.model.User;
import com.train.service.TrainBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TrainBookingController {
    private final TrainBookingService trainBookingService;

    public TrainBookingController(TrainBookingService trainBookingService) {
        this.trainBookingService = trainBookingService;
    }

    @PostMapping
    public ResponseEntity<Ticket> purchaseTicket(@Valid @RequestBody User user) {
        return ResponseEntity.ok(trainBookingService.purchaseTicket(user));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable String ticketId) {
        Ticket ticket = trainBookingService.getTicket(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<List<Ticket>> getTicketsBySection(@PathVariable String section) {
        if (!section.matches("[AB]")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(trainBookingService.getTicketsBySection(section));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> removeTicket(@PathVariable String ticketId) {
        trainBookingService.removeTicket(ticketId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ticketId}/seat")
    public ResponseEntity<Ticket> modifySeat(
            @PathVariable String ticketId,
            @RequestParam String section,
            @RequestParam int seatNumber) {
        if (!section.matches("[AB]") || seatNumber < 1) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Ticket ticket = trainBookingService.modifySeat(ticketId, section, seatNumber);
            if (ticket == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ticket);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 