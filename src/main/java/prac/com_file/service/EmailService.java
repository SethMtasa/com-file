package prac.com_file.service;

import java.util.List;

public interface EmailService {

    boolean sendNotificationEmail(String to, String subject, String message);

    boolean sendFileExpiryNotification(String karEmail, String fileName, int daysUntilExpiry, String fileUrl);

    boolean sendBulkNotification(List<String> emails, String subject, String message);

    boolean sendSystemNotification(String subject, String message);

    boolean sendFileAssignmentNotification(String karEmail, String fileName, String uploadedBy,
                                           String description, String region, String channelPartnerType,
                                           String expiryDate, String fileUrl);
}