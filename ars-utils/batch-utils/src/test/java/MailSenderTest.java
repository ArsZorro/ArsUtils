import java.util.*;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailSenderTest {
    @Test
    public void testMail() throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.ars.ru");
        mailSender.setUsername(System.getProperty("ARS_MAIL_LOGIN"));
        mailSender.setPassword(System.getProperty("ARS_MAIL_PASSWORD"));
        Properties prop = new Properties();
        prop.put("mail.smtp.from", "ars.ars@ars.ru");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.starttls.enable", true);
        prop.put("mail.smtp.localhost", "ars.ru");
        prop.put("mail.smtp.auth", true);

        mailSender.setJavaMailProperties(prop);
        mailSender.testConnection();

        SimpleMailMessage simpleMessage = new SimpleMailMessage();

        // simpleMessage.setFrom("octopus.bic@baltinfocom.ru");
        simpleMessage.setTo("pavel@ars.ru");
        simpleMessage.setSubject("Hello");
        simpleMessage.setText("Hello\nWorld");

        mailSender.send(simpleMessage);
    }
}
