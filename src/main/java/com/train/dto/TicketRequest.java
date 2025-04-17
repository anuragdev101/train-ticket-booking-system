package com.train.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.train.model.User;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank(message = "Departure station is required")
    private String from;
    
    @NotBlank(message = "Destination station is required")
    private String to;
    
    @Valid
    private User user;
} 