package com.bank.monolith.controller;

import com.bank.monolith.common.dto.AccountResponse;
import com.bank.monolith.common.dto.ApiResponse;
import com.bank.monolith.common.dto.CreateAccountRequest;
import com.bank.monolith.common.dto.LoanRequest;
import com.bank.monolith.common.dto.LoanResponse;
import com.bank.monolith.common.dto.TransactionRequest;
import com.bank.monolith.common.dto.TransactionResponse;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.service.AccountService;
import com.bank.monolith.service.LoanService;
import com.bank.monolith.service.ReportService;
import com.bank.monolith.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Operations", description = "Endpoints for Customer Dashboard functionalities")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final ReportService reportService;

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Account created successfully"));
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Process a new transaction (Deposit, Withdrawal, Transfer)")
    public ResponseEntity<ApiResponse<TransactionResponse>> processTransaction(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.processTransaction(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Transaction processed successfully"));
    }

    @PostMapping("/loans/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Apply for a new loan")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LoanRequest request) {
        LoanResponse response = loanService.applyForLoan(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Loan application successful"));
    }

    @PostMapping("/loans/{accountNumber}/pay-emi")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Pay EMI for a specific loan account")
    public ResponseEntity<ApiResponse<Void>> payEmi(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber) {
        loanService.payEmi(user.getId(), accountNumber);
        return ResponseEntity.ok(ApiResponse.success(null, "EMI paid successfully"));
    }

    @GetMapping("/accounts/{accountNumber}/statements")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get paginated account statements")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getStatements(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionResponse> statements = reportService.getAccountStatement(user.getId(), accountNumber, page,
                size);
        return ResponseEntity.ok(ApiResponse.success(statements, "Statements retrieved successfully"));
    }
}
