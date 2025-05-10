package expense.model;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    
    public void sendAlertEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("nithishboyanapalli36@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
