package com.bank.monolith.service;

import com.bank.monolith.common.dto.TransactionRequest;
import com.bank.monolith.common.dto.TransactionResponse;
import com.bank.monolith.domain.entity.SavingsAccount;
import com.bank.monolith.domain.entity.Transaction;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountStatus;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.enums.TransactionType;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private SavingsAccount sourceAccount;
    private SavingsAccount targetAccount;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        sourceAccount = new SavingsAccount();
        sourceAccount.setId(10L);
        sourceAccount.setAccountNumber("SRC12345");
        sourceAccount.setUser(user1);
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setBalance(new BigDecimal("1000.00"));

        targetAccount = new SavingsAccount();
        targetAccount.setId(20L);
        targetAccount.setAccountNumber("TGT67890");
        targetAccount.setUser(user2);
        targetAccount.setStatus(AccountStatus.ACTIVE);
        targetAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void shouldDepositSuccessfully() {
        TransactionRequest req = TransactionRequest.builder()
                .sourceAccountNumber("SRC12345")
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .build();

        when(accountRepository.findByAccountNumber("SRC12345")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any())).thenReturn(sourceAccount);

        Transaction tx = new Transaction();
        tx.setTransactionReference("REF1");
        when(transactionRepository.save(any())).thenReturn(tx);

        TransactionResponse resp = transactionService.processTransaction(1L, req);

        assertThat(resp).isNotNull();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("1500.00");
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void shouldTransferSuccessfully() {
        TransactionRequest req = TransactionRequest.builder()
                .sourceAccountNumber("SRC12345")
                .targetAccountNumber("TGT67890")
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(new BigDecimal("300.00"))
                .build();

        when(accountRepository.findByAccountNumber("SRC12345")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("TGT67890")).thenReturn(Optional.of(targetAccount));

        Transaction tx = new Transaction();
        tx.setTransactionReference("REF2");
        when(transactionRepository.save(any())).thenReturn(tx);

        transactionService.processTransaction(1L, req);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("700.00");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("800.00");

        // Two account saves and two transaction saves (one for source, one for target)
        verify(accountRepository, times(2)).save(any());
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalanceForWithdrawal() {
        TransactionRequest req = TransactionRequest.builder()
                .sourceAccountNumber("SRC12345")
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("2000.00"))
                .build();

        when(accountRepository.findByAccountNumber("SRC12345")).thenReturn(Optional.of(sourceAccount));

        assertThatThrownBy(() -> transactionService.processTransaction(1L, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient balance");
    }
}
