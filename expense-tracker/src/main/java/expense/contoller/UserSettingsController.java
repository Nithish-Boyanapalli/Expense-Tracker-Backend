package expense.contoller;

import expense.model.Users;
import expense.model.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class UserSettingsController {

    @Autowired
    private SettingsService settingsService;

    // Endpoint to get the user's current settings
    @GetMapping("/get-settings/{email}")
    public ResponseEntity<Users> getUserSettings(@PathVariable String email) {
        Users user = settingsService.getUserSettings(email);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to update the user's profile settings
    @PutMapping("/update-profile/{email}")
    public ResponseEntity<Users> updateProfileSettings(@PathVariable String email, 
                                                       @RequestBody Users updatedUser) {
        Users updated = settingsService.updateProfileSettings(
            email, 
            updatedUser.getFullname(), 
            updatedUser.getPhoneNumber(), 
            updatedUser.getProfilePictureUrl(),
            updatedUser.getLanguagePreference()
            
        );

        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to update the user's alert preferences
    @PutMapping("/update-alert-preferences/{email}")
    public ResponseEntity<Users> updateAlertPreferences(@PathVariable String email, 
                                                        @RequestBody boolean receiveAlerts) {
        Users updated = settingsService.updateAlertPreferences(email, receiveAlerts);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to update the user's password
    @PutMapping("/update-password/{email}")
    public ResponseEntity<Users> updatePassword(@PathVariable String email, 
                                               @RequestBody String newPassword) {
        Users updated = settingsService.updatePassword(email, newPassword);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to update the user's profile picture
    @PutMapping("/update-profile-picture/{email}")
    public ResponseEntity<Users> updateProfilePicture(@PathVariable String email, 
                                                      @RequestBody String profilePictureUrl) {
        Users updated = settingsService.updateProfilePicture(email, profilePictureUrl);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
