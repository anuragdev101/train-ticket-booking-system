package com.train.service;

import com.train.model.Ticket;
import com.train.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TrainBookingService {
    private static final double TICKET_PRICE = 20.0;
    private static final int SEATS_PER_SECTION = 50;
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();
    private final Map<String, Set<Integer>> occupiedSeats = new ConcurrentHashMap<>();

    public TrainBookingService() {
        occupiedSeats.put("A", new HashSet<>());
        occupiedSeats.put("B", new HashSet<>());
    }

    public Ticket purchaseTicket(User user) {
        String section = assignSection();
        int seatNumber = assignSeat(section);
        
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setFrom("London");
        ticket.setTo("France");
        ticket.setUser(user);
        ticket.setPrice(TICKET_PRICE);
        ticket.setSection(section);
        ticket.setSeatNumber(seatNumber);

        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Ticket getTicket(String ticketId) {
        return tickets.get(ticketId);
    }

    public List<Ticket> getTicketsBySection(String section) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getSection().equals(section))
                .collect(Collectors.toList());
    }

    public void removeTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            occupiedSeats.get(ticket.getSection()).remove(ticket.getSeatNumber());
            tickets.remove(ticketId);
        }
    }

    public Ticket modifySeat(String ticketId, String newSection, int newSeatNumber) {
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
} 