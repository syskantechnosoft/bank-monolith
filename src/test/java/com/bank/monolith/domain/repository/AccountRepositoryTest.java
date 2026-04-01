package com.bank.monolith.domain.repository;

import com.bank.monolith.domain.entity.SavingsAccount;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountStatus;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("account.owner@example.com")
                .password("password")
                .firstName("Owner")
                .lastName("Test")
                .phoneNumber("1112223334")
                .role(Role.ROLE_CUSTOMER)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void shouldSaveAndFindAccountByAccountNumber() {
        SavingsAccount account = new SavingsAccount();
        account.setAccountNumber("SV1234567890");
        account.setUser(testUser);
        account.setAccountType(AccountType.SAVINGS);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("1000.00"));
        account.setInterestRate(new BigDecimal("5.0"));

        accountRepository.save(account);

        var found = accountRepository.findByAccountNumber("SV1234567890");
        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo("1000.00");
    }
}
