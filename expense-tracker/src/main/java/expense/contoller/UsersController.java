package expense.contoller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import expense.model.Transaction;
import expense.model.Users;
import expense.model.UsersManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expense_users")
@CrossOrigin(origins = "*")
public class UsersController {
    
    @Autowired
    UsersManager UM;
    

    
    @PostMapping("/signup")
    public String signup(@RequestBody Users U) {
        return UM.addUser(U);
    }

    // Step 1: Request Password Reset (Send Token to Email)
    @GetMapping("/forgotpassword/{email}")
    public String forgotPassword(@PathVariable String email) {
        return UM.sendPasswordResetLink(email);
    }

    // Step 2: Reset Password Using Token
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {
        return UM.resetPassword(request.get("token"), request.get("newPassword"));
    }

    // User Sign-In
    @PostMapping("/signin")
    public String signIn(@RequestBody Users U) {
        return UM.validateCredentials(U.getEmail(), U.getPassword());
    }
    
 // Retrieve User Details After Login
    @GetMapping("/getUser/{email}")
    public Users getUserDetails(@PathVariable String email) {
        return UM.getUserDetails(email);
    }
    
    @GetMapping("/transactions/{email}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String email) {
        try {
            List<Transaction> transactions = UM.getTransactionsByEmail(email);
            return ResponseEntity.ok(transactions.isEmpty() ? new ArrayList<>() : transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    @GetMapping("/recent-transactions/{email}")
    public ResponseEntity<List<Transaction>> getRecentTransactions(@PathVariable String email) {
        try {
            List<Transaction> transactions = UM.getRecent5TransactionsByEmail(email);
            return ResponseEntity.ok(transactions.isEmpty() ? new ArrayList<>() : transactions);
        } catch (Exception e) {
            e.printStackTrace(); // Add this for debugging!
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }


    
    // Add new transaction
    @PostMapping("/addTransaction")
    public ResponseEntity<String> addTransaction(@RequestBody Transaction txn) {
        System.out.println("Received Transaction: " + txn);
        String result = UM.addTransaction(txn);
        return result.startsWith("400") ?
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result) :
            ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

// Delete a transaction by ID
    @DeleteMapping("/deleteTransaction/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        String result = UM.deleteTransaction(id);
        if (result.startsWith("404")) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        return ResponseEntity.ok(result);
    }
    
 // Update a transaction
    @PutMapping("/updateTransaction")
    public ResponseEntity<String> updateTransaction(@RequestBody Transaction txn) {
        String result = UM.updateTransaction(txn);
        if (result.startsWith("404")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else if (result.startsWith("500")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.ok(result);
    }



}