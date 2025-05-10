package expense.model;

import expense.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private UsersRepository usersRepository;

    // Method to update user profile settings
    public Users updateProfileSettings(String userEmail, String fullname, String phoneNumber, String profilePictureUrl, 
                                       String languagePreference) {
        // Find the user by email
        Optional<Users> userOpt = usersRepository.findById(userEmail);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();

            // Update the fields
            user.setFullname(fullname);
            user.setPhoneNumber(phoneNumber);
            user.setLanguagePreference(languagePreference);
            

            // Save the updated user back to the database
            return usersRepository.save(user);
        } else {
            // User not found
            return null;
        }
    }

    // Method to update user alert preferences
    public Users updateAlertPreferences(String userEmail, boolean receiveAlerts) {
        Optional<Users> userOpt = usersRepository.findById(userEmail);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setReceiveAlerts(receiveAlerts);

            // Save the updated alert preference
            return usersRepository.save(user);
        } else {
            // User not found
            return null;
        }
    }

    // Method to update password (optional, but useful for settings)
    public Users updatePassword(String userEmail, String newPassword) {
        Optional<Users> userOpt = usersRepository.findById(userEmail);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setPassword(newPassword);

            // Save the updated password
            return usersRepository.save(user);
        } else {
            // User not found
            return null;
        }
    }

    // Method to update profile picture (useful when the user uploads a new one)
    public Users updateProfilePicture(String userEmail, String profilePictureUrl) {
        Optional<Users> userOpt = usersRepository.findById(userEmail);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setProfilePictureUrl(profilePictureUrl);

            // Save the updated profile picture URL
            return usersRepository.save(user);
        } else {
            // User not found
            return null;
        }
    }

    // Method to get user settings (retrieve user settings like profile, preferences)
    public Users getUserSettings(String userEmail) {
        Optional<Users> userOpt = usersRepository.findById(userEmail);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            // User not found
            return null;
        }
    }
}
