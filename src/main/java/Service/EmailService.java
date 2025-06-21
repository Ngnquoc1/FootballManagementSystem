package Service;

import Model.Session;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class EmailService {

    private final String username = "nhuquoc1104@gmail.com";
    private final String password = "lvdn xaxf serz bsdp";

    public void sendEmail(String to, String subject, String content, File attachment) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Body part for the message text
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(content);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        // Attachment part
        if (attachment != null && attachment.exists()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(attachment);
            } catch (Exception e) {
                throw new MessagingException("Failed to attach file", e);
            }
            multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);

        Transport.send(message);
    }
}