package com.bank.monolith.service;

import com.bank.monolith.common.dto.CreateAccountRequest;
import com.bank.monolith.common.dto.LoanRequest;
import com.bank.monolith.domain.entity.LoanAccount;
import com.bank.monolith.domain.entity.Transaction;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.enums.TransactionType;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.TransactionRepository;
import com.bank.monolith.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldApplyForLoanAndCalculateEmi() {
        LoanRequest request = LoanRequest.builder()
                .principalAmount(new BigDecimal("100000"))
                .tenureMonths(12)
                .build();

        User mockUser = new User();
        mockUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        LoanAccount mockLoanAcc = new LoanAccount();
        mockLoanAcc.setId(50L);
        mockLoanAcc.setAccountNumber("LN12345");
        mockLoanAcc.setPrincipalAmount(new BigDecimal("100000"));

        when(accountRepository.save(any(LoanAccount.class))).thenAnswer(invocation -> {
            LoanAccount arg = invocation.getArgument(0);
            arg.setId(50L);
            return arg;
        });

        var response = loanService.applyForLoan(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getEmiAmount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getOutstandingBalance()).isEqualByComparingTo("100000");

        verify(accountRepository, times(1)).save(any(LoanAccount.class));
    }

    @Test
    void shouldPayEmiSuccessfully() {
        LoanAccount loanAccount = new LoanAccount();
        loanAccount.setId(50L);
        loanAccount.setAccountNumber("LN12345");
        loanAccount.setPrincipalAmount(new BigDecimal("100000"));
        loanAccount.setOutstandingBalance(new BigDecimal("100000"));
        loanAccount.setEmiAmount(new BigDecimal("8791.59"));

        User user = new User();
        user.setId(1L);
        loanAccount.setUser(user);

        when(accountRepository.findByAccountNumber("LN12345")).thenReturn(Optional.of(loanAccount));
        when(accountRepository.save(any(LoanAccount.class))).thenReturn(loanAccount);

        loanService.payEmi(1L, "LN12345");

        assertThat(loanAccount.getOutstandingBalance()).isLessThan(new BigDecimal("100000"));
        verify(accountRepository, times(1)).save(loanAccount);
        verify(transactionService, times(1)).processTransaction(eq(1L), any());
    }
}
