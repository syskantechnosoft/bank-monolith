package com.bank.monolith.domain.repository;

import com.bank.monolith.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId, Pageable pageable);

    Page<Transaction> findByAccountIdAndTimestampBetweenOrderByTimestampDesc(Long accountId, LocalDateTime start,
            LocalDateTime end, Pageable pageable);

    boolean existsByTransactionReference(String reference);
}
