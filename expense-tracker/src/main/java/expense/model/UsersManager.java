package expense.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import expense.repository.BudgetRepository;
import expense.repository.TransactionsRepository;
import expense.repository.UsersRepository;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersManager {
    
    @Autowired
    UsersRepository UR;

    @Autowired
    EmailManager EM;

    @Autowired
    JwtManager JWT;
    
    @Autowired
    TransactionsRepository TR;

    // Register a new user
    public String addUser(Users U) {
        if (UR.validateEmail(U.getEmail()) > 0) {
            return "401::Email Already Exist";
        }
        UR.save(U);
        return "200::User Registered Successfully";
    }

    // Step 1: Generate Reset Token & Send Email
    public String sendPasswordResetLink(String email) {
        Optional<Users> userOpt = UR.findById(email);
        if (!userOpt.isPresent()) {
            return "404::Email not registered";
        }

        Users user = userOpt.get();
        String token = UUID.randomUUID().toString();  // Generate unique token
        Date expiryDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000); // 30 min expiry

        user.setResetToken(token);
        user.setTokenExpiry(expiryDate);
        UR.save(user); // Save token in the database

        String resetLink = "http://localhost:5173/reset-password?token=" + token;  // Frontend URL
        String message = String.format("Hello %s,\n\nClick below to reset your password:\n%s\n\nThis link is valid for 30 minutes.",
                                       user.getFullname(), resetLink);

        return EM.sendEmail(user.getEmail(), "Password Reset Request", message);
    }

    // Step 2: Validate Token and Reset Password
    public String resetPassword(String token, String newPassword) {
        Users user = UR.findByResetToken(token);
        if (user == null || user.getTokenExpiry().before(new Date())) {
            return "401::Invalid or Expired Token";
        }

        user.setPassword(newPassword);
        user.setResetToken(null); // Remove token after reset
        user.setTokenExpiry(null);
        UR.save(user);

        return "200::Password Reset Successful";
    }

    // Validate User Credentials for Sign-In
    public String validateCredentials(String email, String password) {
        if (UR.validateCredentials(email, password) > 0) {
            String token = JWT.generateToken(email);
            return "200::" + token;
        }
        return "401::Invalid Credentials";
    }
    
    // Get user details based on email
    public Users getUserDetails(String email) {
        Optional<Users> userOpt = UR.findById(email);
        return userOpt.orElse(null);
    }
    
    public List<Transaction> getTransactionsByEmail(String email) {
        return TR.findByUserEmail(email);
    }
    
    public List<Transaction> getRecent5TransactionsByEmail(String email) {
        Pageable pageable = PageRequest.of(0, 5);
        return TR.findTop5RecentByUserEmail(email, pageable);
    }


    
   // Add a new transaction
    @Autowired
    BudgetRepository budgetRepository;

    // Add a new transaction
    @Transactional
    public String addTransaction(Transaction txn) {
        if (txn.getUser() == null || txn.getCategory() == null || txn.getAmount() <= 0 || txn.getDate() == null) {
            return "400::Invalid Transaction Data";
        }
        if (txn.getPaymentType() == null || txn.getPaymentType().isEmpty()) {
            return "400::Payment Type is required";
        }
        if (txn.getNote() == null) {
            txn.setNote(""); 
        }
        if (txn.getTransactionType() == null || 
           (!txn.getTransactionType().equalsIgnoreCase("income") && 
            !txn.getTransactionType().equalsIgnoreCase("expense"))) {
            return "400 - Invalid transaction type. Must be 'income' or 'expense'.";
        }
        try {
            TR.save(txn);

            // Update spentAmount in Budget if type is Expense
            if (txn.getTransactionType().equalsIgnoreCase("expense")) {
                Budget budget = budgetRepository.findByUserEmailAndCategory(txn.getUser().getEmail(), txn.getCategory());
                if (budget != null) {
                    budget.setSpentAmount(budget.getSpentAmount() + txn.getAmount());
                    budgetRepository.save(budget);
                }
            }

            return "200 - Transaction added successfully";
        } catch (Exception e) {
            return "500 - Error while adding transaction: " + e.getMessage();
        }
    }



    // Delete a transaction by ID
    @Transactional
    public String deleteTransaction(Long id) {
        if (!TR.existsById(id)) {
            return "404::Transaction Not Found";
        }
        try {
            TR.deleteById(id);
            return "200::Transaction Deleted Successfully";
        } catch (Exception e) {
            return "500::Error Deleting Transaction";
        }
    }
    
    @Transactional
    public String updateTransaction(Transaction updatedTxn) {
        if (updatedTxn.getId() == null || !TR.existsById(updatedTxn.getId())) {
            return "404::Transaction Not Found";
        }

        try {
            Transaction existingTxn = TR.findById(updatedTxn.getId()).orElse(null);
            if (existingTxn == null) return "404::Transaction Not Found";

            // Rollback spentAmount from previous transaction
            if (existingTxn.getTransactionType().equalsIgnoreCase("expense")) {
                Budget oldBudget = budgetRepository.findByUserEmailAndCategory(
                    existingTxn.getUser().getEmail(), existingTxn.getCategory());
                if (oldBudget != null) {
                    oldBudget.setSpentAmount(oldBudget.getSpentAmount() - existingTxn.getAmount());
                    budgetRepository.save(oldBudget);
                }
            }

            // Update transaction fields
            existingTxn.setCategory(updatedTxn.getCategory());
            existingTxn.setAmount(updatedTxn.getAmount());
            existingTxn.setDate(updatedTxn.getDate());
            existingTxn.setPaymentType(updatedTxn.getPaymentType());
            existingTxn.setTransactionType(updatedTxn.getTransactionType());
            existingTxn.setNote(updatedTxn.getNote());

            TR.save(existingTxn);

            // Apply new spentAmount if still expense
            if (updatedTxn.getTransactionType().equalsIgnoreCase("expense")) {
                Budget newBudget = budgetRepository.findByUserEmailAndCategory(
                    updatedTxn.getUser().getEmail(), updatedTxn.getCategory());
                if (newBudget != null) {
                    newBudget.setSpentAmount(newBudget.getSpentAmount() + updatedTxn.getAmount());
                    budgetRepository.save(newBudget);
                }
            }

            return "200::Transaction Updated Successfully";
        } catch (Exception e) {
            return "500::Error Updating Transaction";
        }
    }




}