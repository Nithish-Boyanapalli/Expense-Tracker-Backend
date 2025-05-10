package expense.model;

import expense.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {
    private static final double WARNING_THRESHOLD = 0.8; // 80% threshold for warnings
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private EmailService emailService;

    public List<Budget> getBudgetsByUser(String userEmail) {
        return budgetRepository.findByUserEmail(userEmail);
    }

    public Budget addBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
    
    public void updateSpentAmount(String userEmail, String category, double amount, boolean isAddition) {
        Budget budget = budgetRepository.findByUserEmailAndCategory(userEmail, category);
        if (budget != null) {
            double newSpent = isAddition ? 
                budget.getSpentAmount() + amount : 
                budget.getSpentAmount() - amount;
            newSpent = Math.max(newSpent, 0); // Prevent negatives
            
            budget.setSpentAmount(newSpent);
            budgetRepository.save(budget);
            
            // Check and send alerts
            checkBudgetStatusAndNotify(budget);
        }
    }

    private void checkBudgetStatusAndNotify(Budget budget) {
        double percentageUsed = budget.getSpentAmount() / budget.getBudgetAmount();
        
        if (percentageUsed >= WARNING_THRESHOLD && percentageUsed < 1.0) {
            sendBudgetWarningEmail(budget, percentageUsed);
        } 
        else if (percentageUsed >= 1.0) {
            sendBudgetExceededEmail(budget);
        }
    }

    private void sendBudgetWarningEmail(Budget budget, double percentageUsed) {
        String subject = "Budget Warning: " + budget.getCategory();
        String message = String.format(
            "Hello,\n\nYou've used %.1f%% of your %s budget (%.2f/%.2f).\n" +
            "Consider reviewing your spending to stay within budget.\n\n" +
            "Best regards,\nArtha Guru",
            percentageUsed * 100,
            budget.getCategory(),
            budget.getSpentAmount(),
            budget.getBudgetAmount()
        );
        
        emailService.sendAlertEmail(budget.getUserEmail(), subject, message);
    }

    private void sendBudgetExceededEmail(Budget budget) {
        String subject = "Budget Exceeded: " + budget.getCategory();
        String message = String.format(
            "Hello,\n\nYou've exceeded your %s budget by %.2f.\n" +
            "Current spending: %.2f\nBudget limit: %.2f\n\n" +
            "Consider adjusting your expenses.\n\n" +
            "Best regards,\nArtha Guru",
            budget.getCategory(),
            budget.getSpentAmount() - budget.getBudgetAmount(),
            budget.getSpentAmount(),
            budget.getBudgetAmount()
        );
        
        emailService.sendAlertEmail(budget.getUserEmail(), subject, message);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetBudgetsMonthly() {
        List<Budget> budgets = budgetRepository.findAll();
        for (Budget budget : budgets) {
            budget.setSpentAmount(0);
            budget.setResetDate(LocalDate.now().plusMonths(1));
            budgetRepository.save(budget);
            
            String subject = "Budget Reset: " + budget.getCategory();
            String message = String.format(
                "Hello,\n\nYour %s budget has been reset for the new month.\n" +
                "New budget amount: %.2f\n\nHappy budgeting!\n\n" +
                "Best regards,\nArtha Guru",
                budget.getCategory(),
                budget.getBudgetAmount()
            );
            
            emailService.sendAlertEmail(budget.getUserEmail(), subject, message);
        }
    }
}