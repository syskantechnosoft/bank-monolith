package com.bank.monolith.domain.repository;

import com.bank.monolith.domain.entity.CurrentAccount;
import com.bank.monolith.domain.entity.Transaction;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountStatus;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.enums.Role;
import com.bank.monolith.domain.enums.TransactionStatus;
import com.bank.monolith.domain.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private CurrentAccount testAccount;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("tx.owner@example.com")
                .password("password")
                .firstName("TxOwner")
                .lastName("Test")
                .phoneNumber("9998887776")
                .role(Role.ROLE_CUSTOMER)
                .build();
        userRepository.save(user);

        testAccount = new CurrentAccount();
        testAccount.setAccountNumber("TX1111222233");
        testAccount.setUser(user);
        testAccount.setAccountType(AccountType.CURRENT);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setBalance(new BigDecimal("500.00"));
        testAccount.setOverdraftLimit(new BigDecimal("1000.00"));
        accountRepository.save(testAccount);
    }

    @Test
    void shouldSaveAndRetrieveTransactionPage() {
        Transaction tx = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .account(testAccount)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("200.00"))
                .balanceAfterTransaction(new BigDecimal("700.00"))
                .status(TransactionStatus.COMPLETED)
                .description("Initial Deposit")
                .build();

        transactionRepository.save(tx);

        var page = transactionRepository.findByAccountIdOrderByTimestampDesc(
                testAccount.getId(),
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getAmount()).isEqualByComparingTo("200.00");
    }
}
