package com.bank.monolith.service;

import com.bank.monolith.common.dto.AccountResponse;
import com.bank.monolith.common.dto.CreateAccountRequest;
import com.bank.monolith.domain.entity.*;
import com.bank.monolith.domain.enums.AccountStatus;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Transactional
    public AccountResponse createAccount(Long userId, CreateAccountRequest request) {
        log.info("Creating new {} account for user {}", request.getAccountType(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account;
        switch (request.getAccountType()) {
            case SAVINGS -> {
                SavingsAccount sa = new SavingsAccount();
                sa.setInterestRate(new BigDecimal("4.0"));
                account = sa;
            }
            case CURRENT -> {
                CurrentAccount ca = new CurrentAccount();
                ca.setOverdraftLimit(new BigDecimal("5000.00"));
                account = ca;
            }
            case LOAN -> {
                LoanAccount la = new LoanAccount();
                la.setInterestRate(new BigDecimal("10.0"));
                account = la;
            }
            case DEPOSIT -> {
                DepositAccount da = new DepositAccount();
                da.setInterestRate(new BigDecimal("6.5"));
                account = da;
            }
            default -> throw new IllegalArgumentException("Unknown account type");
        }

        account.setAccountNumber(generateUniqueAccountNumber());
        account.setUser(user);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        account = accountRepository.save(account);

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateUniqueAccountNumber() {
        String actNumber;
        do {
            actNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(actNumber));
        return actNumber;
    }
}
