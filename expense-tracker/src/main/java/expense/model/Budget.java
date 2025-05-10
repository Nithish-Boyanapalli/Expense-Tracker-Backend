package expense.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "budgets")
@Data
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;  // Connects to User via email (foreign key-like)

    private String category;
    
    private double budgetAmount;

    private double spentAmount;
    
    @Column(name = "reset_date")
    private LocalDate resetDate; // New field to track reset date

    public Budget() {}

    public Budget(String userEmail, String category, double budgetAmount, double spentAmount, LocalDate resetDate) {
        this.userEmail = userEmail;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.resetDate = resetDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }
    
    // ✅ Getter and Setter for resetDate
    public LocalDate getResetDate() {
        return resetDate;
    }

    public void setResetDate(LocalDate resetDate) {
        this.resetDate = resetDate;
    }
    
    // ✅ Prevents future budget dates
    @PrePersist
    @PreUpdate
    private void validateDate() {
        if (resetDate != null && resetDate.isAfter(LocalDate.now().plusMonths(1))) {
            throw new IllegalArgumentException("Cannot set a budget for future dates.");
        }
    }
}