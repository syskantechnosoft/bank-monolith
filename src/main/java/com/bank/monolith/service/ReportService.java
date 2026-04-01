package com.bank.monolith.service;

import com.bank.monolith.common.dto.TransactionResponse;
import com.bank.monolith.domain.entity.Account;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAccountStatement(Long userId, String accountNumber, int page, int size) {
        log.info("Fetching account statement for {}, page: {}, size: {}", accountNumber, page, size);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to access account statment");
        }

        return transactionRepository
                .findByAccountIdOrderByTimestampDesc(account.getId(), PageRequest.of(page, size))
                .map(tx -> TransactionResponse.builder()
                        .transactionReference(tx.getTransactionReference())
                        .sourceAccountNumber(tx.getAccount().getAccountNumber())
                        .targetAccountNumber(tx.getRelatedAccountNumber())
                        .type(tx.getType())
                        .amount(tx.getAmount())
                        .status(tx.getStatus())
                        .timestamp(tx.getTimestamp())
                        .description(tx.getDescription())
                        .build());
    }
}
