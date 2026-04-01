package com.bank.monolith.service;

import com.bank.monolith.common.dto.TransactionRequest;
import com.bank.monolith.common.dto.TransactionResponse;
import com.bank.monolith.domain.entity.Account;
import com.bank.monolith.domain.entity.Transaction;
import com.bank.monolith.domain.enums.TransactionStatus;
import com.bank.monolith.domain.enums.TransactionType;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse processTransaction(Long userId, TransactionRequest request) {
        log.info("Processing {} for account {}", request.getTransactionType(), request.getSourceAccountNumber());

        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        if (!sourceAccount.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to access source account");
        }

        switch (request.getTransactionType()) {
            case DEPOSIT -> {
                sourceAccount.credit(request.getAmount());
                accountRepository.save(sourceAccount);
                return createAndSaveTransaction(sourceAccount, request.getTransactionType(), request,
                        sourceAccount.getAccountNumber());
            }
            case WITHDRAWAL -> {
                sourceAccount.debit(request.getAmount());
                accountRepository.save(sourceAccount);
                return createAndSaveTransaction(sourceAccount, request.getTransactionType(), request,
                        sourceAccount.getAccountNumber());
            }
            case TRANSFER_OUT -> {
                if (request.getTargetAccountNumber() == null || request.getTargetAccountNumber().isBlank()) {
                    throw new IllegalArgumentException("Target account is required for transfer");
                }
                Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                        .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

                // Debit source
                sourceAccount.debit(request.getAmount());
                accountRepository.save(sourceAccount);
                TransactionResponse response = createAndSaveTransaction(sourceAccount, TransactionType.TRANSFER_OUT,
                        request, request.getTargetAccountNumber());

                // Credit target
                targetAccount.credit(request.getAmount());
                accountRepository.save(targetAccount);
                createAndSaveTransaction(targetAccount, TransactionType.TRANSFER_IN, request,
                        request.getSourceAccountNumber());

                return response;
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type");
        }
    }

    private TransactionResponse createAndSaveTransaction(Account account, TransactionType type,
            TransactionRequest request, String relatedAccount) {
        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .account(account)
                .type(type)
                .amount(request.getAmount())
                .balanceAfterTransaction(account.getBalance())
                .relatedAccountNumber(relatedAccount)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription())
                .build();

        transaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionReference(transaction.getTransactionReference())
                .sourceAccountNumber(account.getAccountNumber())
                .targetAccountNumber(relatedAccount)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .description(transaction.getDescription())
                .build();
    }
}
