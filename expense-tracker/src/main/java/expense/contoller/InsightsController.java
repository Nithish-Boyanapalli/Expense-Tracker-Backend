package expense.contoller;

import expense.model.InsightsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/insights")
@CrossOrigin(origins = "*")
public class InsightsController {

    @Autowired
    private InsightsManager insightsManager;

    @GetMapping("/monthly-summary/{email}")
    public Map<String, Double> getMonthlySummary(@PathVariable String email) {
        return insightsManager.calculateMonthlySummary(email);
    }

    @GetMapping("/category-wise-summary/{email}")
    public Map<String, Double> getCategoryWiseSpending(@PathVariable String email) {
        return insightsManager.getCategoryWiseExpenses(email);
    }

    @GetMapping("/prediction/{email}")
    public Map<String, Object> predictNextMonthExpense(@PathVariable String email) {
        return insightsManager.predictNextMonthExpense(email);
    }

    @GetMapping("/smart-suggestions/{email}")
    public List<String> getSmartSuggestions(@PathVariable String email) {
        return insightsManager.generateSmartSuggestions(email);
    }
}