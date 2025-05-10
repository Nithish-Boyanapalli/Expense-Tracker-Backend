package expense.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_users")
public class Users {
    
    @Column(name = "fullname")
    private String fullname;
    
    @Id
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "reset_token")
    private String resetToken;  

    @Column(name = "token_expiry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpiry;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Transaction> userTransactions;  // Transactions linked to the user
    
    @Column(name = "receive_alerts")
    private boolean receiveAlerts = true; // Default is true, if the user wants to receive alerts or not

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // URL for the user's profile picture

    @Column(name = "phone_number")
    private String phoneNumber; // User's phone number (can be used for verification, contact purposes)

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin; // Timestamp of the last login for tracking user activity

    @Column(name = "language_preference")
    private String languagePreference; // User's preferred language (e.g., "en", "fr", "es", etc.)

    

    // Getters and Setters
    public String getFullname() { 
        return fullname;
    }
    public void setFullname(String fullname) { 
        this.fullname = fullname; 
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) {
        this.password = password; 
    }

    public String getResetToken() { 
        return resetToken; 
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Date getTokenExpiry() { 
        return tokenExpiry; 
    }
    public void setTokenExpiry(Date tokenExpiry) { 
        this.tokenExpiry = tokenExpiry; 
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public void setUserTransactions(List<Transaction> userTransactions) {
        this.userTransactions = userTransactions;
    }

    public boolean isReceiveAlerts() {
        return receiveAlerts;
    }

    public void setReceiveAlerts(boolean receiveAlerts) {
        this.receiveAlerts = receiveAlerts;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    

    @Override
    public String toString() {
        return "Users [fullname=" + fullname + ", email=" + email + ", password=" + password + 
               ", receiveAlerts=" + receiveAlerts + ", profilePictureUrl=" + profilePictureUrl + 
               ", phoneNumber=" + phoneNumber + ", lastLogin=" + lastLogin + ", languagePreference=" + languagePreference + "]";
    }
}
