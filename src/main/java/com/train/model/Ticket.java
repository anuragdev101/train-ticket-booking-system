package com.train.model;

import lombok.Data;

@Data
public class Ticket {
    private String id;
    private String from;
    private String to;
    private User user;
    private double price;
    private String section;
    private int seatNumber;
} 