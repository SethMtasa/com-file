package prac.com_file.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import prac.com_file.config.NotificationConfig;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final NotificationConfig notificationConfig;

    public EmailServiceImpl(JavaMailSender mailSender, NotificationConfig notificationConfig) {
        this.mailSender = mailSender;
        this.notificationConfig = notificationConfig;
    }

    @Override
    public boolean sendNotificationEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Set from address (your filemanagement@me.co.lk)
            mailMessage.setFrom("CommercialFileManagement@netone.co.zw");

            // Set recipient - if to is null, use the configured notification email
            if (to == null || to.trim().isEmpty()) {
                mailMessage.setTo(notificationConfig.getEmail()); // all@me.co.lk
            } else {
                mailMessage.setTo(to);
            }

            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            System.out.println("Email sent successfully to: " + (to != null ? to : notificationConfig.getEmail()));
            return true;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendFileExpiryNotification(String karEmail, String fileName, int daysUntilExpiry, String fileUrl) {
        try {
            String subject = "File Expiry Reminder - Action Required";
            String message = String.format(
                    "Dear KAR,\n\n" +
                            "This is to inform you that the following file will expire soon:\n\n" +
                            "File Name: %s\n" +
                            "Days Until Expiry: %d\n" +
                            "File URL: %s\n\n" +
                            "Please review the file and take necessary action before it expires.\n\n" +
                            "Best regards,\n" +
                            "File Management System",
                    fileName, daysUntilExpiry, fileUrl
            );

            return sendNotificationEmail(karEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send file expiry notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendBulkNotification(List<String> emails, String subject, String message) {
        try {
            if (emails == null || emails.isEmpty()) {
                // Send to default notification email if no specific emails provided
                return sendNotificationEmail(null, subject, message);
            }

            boolean allSent = true;
            for (String email : emails) {
                boolean sent = sendNotificationEmail(email, subject, message);
                if (!sent) {
                    allSent = false;
                }
            }
            return allSent;
        } catch (Exception e) {
            System.err.println("Failed to send bulk notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendSystemNotification(String subject, String message) {
        // Always send system notifications to the configured notification email
        return sendNotificationEmail(notificationConfig.getEmail(), subject, message);
    }

    @Override
    public boolean sendFileAssignmentNotification(String karEmail, String fileName, String uploadedBy,
                                                  String description, String region, String channelPartnerType,
                                                  String expiryDate, String fileUrl) {
        try {
            String subject = "New File Assigned - Action Required";
            String message = String.format(
                    "Dear KAR,\n\n" +
                            "A new file has been assigned to you in the File Management System.\n\n" +
                            "File Details:\n" +
                            "• File Name: %s\n" +
                            "• Uploaded By: %s\n" +
                            "• Description: %s\n" +
                            "• Region: %s\n" +
                            "• Channel Partner Type: %s\n" +
                            "• Expiry Date: %s\n" +
//                            "• File URL: %s\n\n" +
                            "Please review the file and ensure all details are correct. " +
                            "You are responsible for managing this file until its expiry date.\n\n" +
                            "Best regards,\n" +
                            "File Management System",
                    fileName,
                    uploadedBy,
                    description != null ? description : "N/A",
                    region,
                    channelPartnerType,
                    expiryDate,
                    fileUrl
            );

            return sendNotificationEmail(karEmail, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send file assignment notification: " + e.getMessage());
            return false;
        }
    }

}