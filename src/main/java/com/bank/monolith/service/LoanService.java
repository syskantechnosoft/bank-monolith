package com.bank.monolith.service;

import com.bank.monolith.common.dto.LoanRequest;
import com.bank.monolith.common.dto.LoanResponse;
import com.bank.monolith.common.dto.TransactionRequest;
import com.bank.monolith.domain.entity.LoanAccount;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.AccountStatus;
import com.bank.monolith.domain.enums.AccountType;
import com.bank.monolith.domain.enums.TransactionType;
import com.bank.monolith.domain.repository.AccountRepository;
import com.bank.monolith.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final Random random = new Random();

    // Default Annual Interest Rate for Loans (10%)
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("10.0");

    @Transactional
    public LoanResponse applyForLoan(Long userId, LoanRequest request) {
        log.info("Applying for loan for user {}, Principal: {}, Tenure: {}", userId, request.getPrincipalAmount(),
                request.getTenureMonths());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal emi = calculateEMI(request.getPrincipalAmount(), ANNUAL_INTEREST_RATE, request.getTenureMonths());

        LoanAccount loanAccount = new LoanAccount();
        loanAccount.setAccountType(AccountType.LOAN);
        loanAccount.setAccountNumber(generateUniqueAccountNumber());
        loanAccount.setUser(user);
        loanAccount.setStatus(AccountStatus.ACTIVE);
        loanAccount.setBalance(BigDecimal.ZERO); // Balance usually reflects current holding or deposit component, we
                                                 // use outstandingBalance for debts
        loanAccount.setPrincipalAmount(request.getPrincipalAmount());
        loanAccount.setInterestRate(ANNUAL_INTEREST_RATE);
        loanAccount.setTenureMonths(request.getTenureMonths());
        loanAccount.setEmiAmount(emi);
        loanAccount.setOutstandingBalance(request.getPrincipalAmount());

        loanAccount = accountRepository.save(loanAccount);

        return LoanResponse.builder()
                .accountNumber(loanAccount.getAccountNumber())
                .principalAmount(loanAccount.getPrincipalAmount())
                .interestRate(loanAccount.getInterestRate())
                .tenureMonths(loanAccount.getTenureMonths())
                .emiAmount(loanAccount.getEmiAmount())
                .outstandingBalance(loanAccount.getOutstandingBalance())
                .createdAt(loanAccount.getCreatedAt())
                .build();
    }

    @Transactional
    public void payEmi(Long userId, String loanAccountNumber) {
        log.info("Processing EMI payment for Loan Account: {}", loanAccountNumber);

        LoanAccount loanAccount = (LoanAccount) accountRepository.findByAccountNumber(loanAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Loan account not found"));

        if (!loanAccount.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to loan account");
        }

        if (loanAccount.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Loan is already fully repaid");
        }

        BigDecimal emiAmount = loanAccount.getEmiAmount();

        // If the outstanding is less than EMI, adjust the EMI
        if (loanAccount.getOutstandingBalance().compareTo(emiAmount) < 0) {
            emiAmount = loanAccount.getOutstandingBalance();
        }

        // Technically, a user pays from a Savings/Current account,
        // but for this method, we might just mark the EMI as paid directly or process a
        // transaction
        // from a specific source.
        // We'll process an EMI_PAYMENT transaction.
        TransactionRequest txRequest = TransactionRequest.builder()
                .sourceAccountNumber(loanAccountNumber)
                .transactionType(TransactionType.EMI_PAYMENT)
                .amount(emiAmount)
                .description("Monthly EMI Payment")
                .build();

        transactionService.processTransaction(userId, txRequest);

        // Reduce outstanding balance
        BigDecimal newOutstanding = loanAccount.getOutstandingBalance().subtract(emiAmount);
        if (newOutstanding.compareTo(BigDecimal.ZERO) < 0) {
            newOutstanding = BigDecimal.ZERO;
        }
        loanAccount.setOutstandingBalance(newOutstanding);

        if (newOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            loanAccount.setStatus(AccountStatus.CLOSED);
        }

        accountRepository.save(loanAccount);
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualInterestRate, int tenureMonths) {
        // r = Monthly interest rate = Annual / 12 / 100
        BigDecimal monthlyRate = annualInterestRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        // (1 + r)^n
        BigDecimal onePlusRToN = monthlyRate.add(BigDecimal.ONE).pow(tenureMonths);

        // EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRToN);
        BigDecimal denominator = onePlusRToN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private String generateUniqueAccountNumber() {
        String actNumber;
        do {
            StringBuilder sb = new StringBuilder(12);
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(10));
            }
            actNumber = sb.toString();
        } while (accountRepository.existsByAccountNumber(actNumber));
        return actNumber;
    }
}
