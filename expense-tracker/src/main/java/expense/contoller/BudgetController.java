package expense.contoller;

import expense.model.Budget;
import expense.model.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
@CrossOrigin(origins = "*") // React fronted port
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // Fetch all budgets by user
    @GetMapping("/{email}")
    public List<Budget> getBudgets(@PathVariable String email) {
        return budgetService.getBudgetsByUser(email);
    }

    // Add new budget
    @PostMapping("/add")
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetService.addBudget(budget);
    }

    // Delete budget
    @DeleteMapping("/delete/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
    }
    
 // ✅ Manual reset endpoint for testing (Optional)
    @PostMapping("/reset")
    public String resetBudgets() {
        budgetService.resetBudgetsMonthly();
        return "Budgets reset successfully.";
    }
}