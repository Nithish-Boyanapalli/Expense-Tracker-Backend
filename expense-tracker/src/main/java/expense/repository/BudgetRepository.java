package expense.repository;

import expense.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUserEmail(String userEmail);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.email = :email AND t.category = :category AND t.transactionType = 'Expense'")
    double sumExpensesForCategory(@Param("email") String email, @Param("category") String category);

    Budget findByUserEmailAndCategory(String userEmail, String category);

}