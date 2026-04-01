package com.bank.monolith.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deposit_accounts")
public class DepositAccount extends Account {
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private LocalDate maturityDate;
    private BigDecimal maturityAmount;
}
