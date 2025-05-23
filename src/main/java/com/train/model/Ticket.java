package com.train.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String id;
    private String from;
    private String to;
    private User user;
    private double price;
    private String section;
    private int seatNumber;
} 