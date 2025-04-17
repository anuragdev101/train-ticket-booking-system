package com.train.service;

import com.train.model.Ticket;
import com.train.model.User;
import com.train.dto.TicketRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TrainBookingService {
    private static final int SEATS_PER_SECTION = 50;
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> occupiedSeats = new ConcurrentHashMap<>();
    private final RouteService routeService;

    public TrainBookingService(RouteService routeService) {
        this.routeService = routeService;
        occupiedSeats.put("A", new HashSet<>());
        occupiedSeats.put("B", new HashSet<>());
    }

    public Ticket purchaseTicket(TicketRequest request) {
        if (request == null || request.getUser() == null) {
            throw new IllegalArgumentException("Invalid ticket request");
        }

        // Check if user already has a ticket for this route
        if (hasExistingTicket(request.getUser(), request.getFrom(), request.getTo())) {
            throw new IllegalStateException("User already has a ticket for this route");
        }

        // Validate and calculate price for the route
        double price = routeService.calculatePrice(request.getFrom(), request.getTo());
        
        String section = assignSection();
        int seatNumber = assignSeat(section);
        
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .from(request.getFrom())
                .to(request.getTo())
                .user(request.getUser())
                .price(price)
                .section(section)
                .seatNumber(seatNumber)
                .build();

        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    private boolean hasExistingTicket(User user, String from, String to) {
        return tickets.values().stream()
                .anyMatch(ticket -> 
                    ticket.getUser().getEmail().equals(user.getEmail()) &&
                    ticket.getFrom().equals(from) &&
                    ticket.getTo().equals(to));
    }

    public Ticket getTicket(String ticketId) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty");
        }
        return tickets.get(ticketId);
    }

    public List<Ticket> getTicketsBySection(String section) {
        if (section == null || !section.matches("[AB]")) {
            throw new IllegalArgumentException("Invalid section. Must be either A or B");
        }
        return tickets.values().stream()
                .filter(ticket -> ticket.getSection().equals(section))
                .collect(Collectors.toList());
    }

    public void removeTicket(String ticketId) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty");
        }
        
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            occupiedSeats.get(ticket.getSection()).remove(ticket.getSeatNumber());
            tickets.remove(ticketId);
        }
    }

    public Ticket modifySeat(String ticketId, String newSection, int newSeatNumber) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty");
        }
        if (newSection == null || !newSection.matches("[AB]")) {
            throw new IllegalArgumentException("Invalid section. Must be either A or B");
        }
        if (newSeatNumber < 1 || newSeatNumber > SEATS_PER_SECTION) {
            throw new IllegalArgumentException("Invalid seat number. Must be between 1 and " + SEATS_PER_SECTION);
        }

        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) {
            return null;
        }

        if (isSeatOccupied(newSection, newSeatNumber)) {
            throw new IllegalStateException("Seat is already occupied");
        }

        // Remove old seat
        occupiedSeats.get(ticket.getSection()).remove(ticket.getSeatNumber());

        // Assign new seat
        ticket.setSection(newSection);
        ticket.setSeatNumber(newSeatNumber);
        occupiedSeats.get(newSection).add(newSeatNumber);

        return ticket;
    }

    private String assignSection() {
        int sectionACount = occupiedSeats.get("A").size();
        int sectionBCount = occupiedSeats.get("B").size();

        if (sectionACount >= SEATS_PER_SECTION && sectionBCount >= SEATS_PER_SECTION) {
            throw new IllegalStateException("No seats available in any section");
        }

        return sectionACount <= sectionBCount ? "A" : "B";
    }

    private int assignSeat(String section) {
        Set<Integer> occupied = occupiedSeats.get(section);
        for (int i = 1; i <= SEATS_PER_SECTION; i++) {
            if (!occupied.contains(i)) {
                occupied.add(i);
                return i;
            }
        }
        throw new IllegalStateException("No seats available in section " + section);
    }

    private boolean isSeatOccupied(String section, int seatNumber) {
        return occupiedSeats.get(section).contains(seatNumber);
    }
    
    public Set<String> getAllStations() {
        return routeService.getAllStations();
    }
} 