package com.bank.monolith.service;

import com.bank.monolith.common.dto.AccountResponse;
import com.bank.monolith.common.dto.CreateAccountRequest;
import com.bank.monolith.domain.entity.SavingsAccount;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.repository.AccountRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateSavingsAccountSuccessfully() {
        CreateAccountRequest req = CreateAccountRequest.builder()
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("1000.00"))
                .build();

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        SavingsAccount savedAccount = new SavingsAccount();
        savedAccount.setId(101L);
        savedAccount.setAccountNumber("123456789012");
        savedAccount.setAccountType(AccountType.SAVINGS);
        savedAccount.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.save(any())).thenReturn(savedAccount);

        AccountResponse resp = accountService.createAccount(1L, req);

        assertThat(resp).isNotNull();
        assertThat(resp.getAccountNumber()).isEqualTo("123456789012");
        assertThat(resp.getBalance()).isEqualByComparingTo("1000.00");
        verify(accountRepository, times(1)).save(any());
    }
}
