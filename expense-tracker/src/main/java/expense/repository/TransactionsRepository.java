package expense.repository;

import expense.model.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // ✅ Fix: Ensures this class is recognized as a repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    // ✅ Fix: Fetch transactions by user's email (Must match the entity relationship)
    List<Transaction> findByUserEmail(String email);

    // ✅ Fix: Use a custom query to avoid potential null values
    @Query("SELECT t FROM Transaction t WHERE t.user.email = :email")
    List<Transaction> getTransactionsByUserEmail(String email);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.email = :email ORDER BY t.date DESC")
    List<Transaction> findTop5RecentByUserEmail(@Param("email") String email, org.springframework.data.domain.Pageable pageable);
}