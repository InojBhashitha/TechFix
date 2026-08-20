package com.techfix.api.repositories;

import com.techfix.api.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRepairRequestId(Long repairRequestId);
    Optional<Payment> findByTransactionReference(String transactionReference);
}
