package com.example.carproject.dto;

import com.example.carproject.domain.AccidentHistoryTicket;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentHistoryTicketDto {
    private Long id;
    private LocalDateTime purchaseDate;
    private Integer validDays;
    private Integer remainingCount;
    private LocalDateTime expireDate;
    private String status;

    private long remainDays;
}
