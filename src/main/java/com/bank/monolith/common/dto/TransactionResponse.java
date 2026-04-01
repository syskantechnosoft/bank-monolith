package com.bank.monolith.common.dto;

import com.bank.monolith.domain.enums.TransactionStatus;
import com.bank.monolith.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String transactionReference;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private String description;
}
