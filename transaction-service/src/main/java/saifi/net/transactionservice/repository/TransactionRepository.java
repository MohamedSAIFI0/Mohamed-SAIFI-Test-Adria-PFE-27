package saifi.net.transactionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saifi.net.transactionservice.domain.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
