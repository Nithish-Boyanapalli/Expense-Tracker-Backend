package expense.model;

import expense.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightsManager {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public Map<String, Double> calculateMonthlySummary(String email) {
        List<Transaction> transactions = transactionsRepository.findByUserEmail(email);
        Map<String, Double> monthlySummary = new TreeMap<>();

        for (Transaction txn : transactions) {
            if (!txn.getTransactionType().equalsIgnoreCase("Expense")) continue;

            LocalDate date = txn.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.getYear();
            monthlySummary.put(month, monthlySummary.getOrDefault(month, 0.0) + txn.getAmount());
        }

        return monthlySummary;
    }

    public Map<String, Double> getCategoryWiseExpenses(String email) {
        List<Transaction> transactions = transactionsRepository.findByUserEmail(email);
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction txn : transactions) {
            if (!txn.getTransactionType().equalsIgnoreCase("Expense")) continue;
            String cat = txn.getCategory();
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + txn.getAmount());
        }

        return categoryMap;
    }

    public Map<String, Object> predictNextMonthExpense(String email) {
        Map<String, Double> monthly = calculateMonthlySummary(email);
        List<Double> y = new ArrayList<>(monthly.values());
        List<Integer> x = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) x.add(i + 1);

        if (x.size() < 2) return Map.of("error", "Not enough data for prediction");

        // Linear Regression: y = a + b * x
        double n = x.size();
        double sumX = x.stream().mapToDouble(d -> d).sum();
        double sumY = y.stream().mapToDouble(d -> d).sum();
        double sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i);
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;
        double prediction = a + b * (n + 1);

        return Map.of("nextMonthPrediction", Math.max(prediction, 0), "previousMonths", monthly);
    }

    public List<String> generateSmartSuggestions(String email) {
        Map<String, Double> categoryWise = getCategoryWiseExpenses(email);

        return categoryWise.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(3)
            .map(entry -> String.format("Reduce '%s' by 20%% to save ₹%.2f",
                    entry.getKey(), entry.getValue() * 0.2))
            .collect(Collectors.toList());
    }
}