package prac.com_file.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import prac.com_file.config.NotificationConfig;
import prac.com_file.dto.ApiResponse;
import prac.com_file.model.File;
import prac.com_file.repository.FileRepository;

import java.util.List;

@Service
public class NotificationSchedulerService {

    private final FileRepository fileRepository;
    private final FileNotificationService fileNotificationService;
    private final EmailService emailService;
    private final NotificationConfig notificationConfig;

    public NotificationSchedulerService(FileRepository fileRepository,
                                        FileNotificationService fileNotificationService,
                                        EmailService emailService,
                                        NotificationConfig notificationConfig) {
        this.fileRepository = fileRepository;
        this.fileNotificationService = fileNotificationService;
        this.emailService = emailService;
        this.notificationConfig = notificationConfig;
    }

    @Scheduled(cron = "${notification.cron_expiring_soon}")
    public void checkExpiringFilesAndNotify() {
        try {
            System.out.println("Checking for expiring files...");

            // Use repository directly to get File entities
            List<File> expiringFiles = fileRepository.findFilesExpiringInDays(notificationConfig.getInterval());

            if (!expiringFiles.isEmpty()) {
                for (File file : expiringFiles) {
                    try {
                        // Create notification in database
                        fileNotificationService.createExpiryNotification(file, notificationConfig.getInterval());

                        // Send email notification to KAR
                        if (file.getAssignedKAR() != null && file.getAssignedKAR().getEmail() != null) {
                            boolean emailSent = emailService.sendFileExpiryNotification(
                                    file.getAssignedKAR().getEmail(),
                                    file.getFileName(),
                                    notificationConfig.getInterval(),
                                    file.getFileUrl()
                            );

                            if (emailSent) {
                                System.out.println("Expiry notification sent for file: " + file.getFileName());
                            } else {
                                System.err.println("Failed to send email for file: " + file.getFileName());
                            }
                        }

                    } catch (Exception e) {
                        System.err.println("Error processing file " + file.getFileName() + ": " + e.getMessage());
                    }
                }

                // Send system notification summary
                String summarySubject = "File Expiry Check Summary";
                String summaryMessage = String.format(
                        "File expiry check completed. Found %d files expiring in %d days.",
                        expiringFiles.size(), notificationConfig.getInterval()
                );
                emailService.sendSystemNotification(summarySubject, summaryMessage);
            } else {
                System.out.println("No expiring files found.");
            }

        } catch (Exception e) {
            System.err.println("Error in expiring files check: " + e.getMessage());
            emailService.sendSystemNotification(
                    "File Management System Error",
                    "Error occurred during expiring files check: " + e.getMessage()
            );
        }
    }

    /**
     * Scheduled task to send pending notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void sendPendingNotifications() {
        try {
            System.out.println("Sending pending notifications...");
            ApiResponse<String> result = fileNotificationService.sendPendingNotifications();

            if (result.success()) {
                System.out.println("Pending notifications processed: " + result.message());
            } else {
                System.err.println("Failed to process pending notifications: " + result.message());
            }
        } catch (Exception e) {
            System.err.println("Error sending pending notifications: " + e.getMessage());
        }
    }

    /**
     * Cleanup old notifications (runs daily at 2 AM)
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void cleanupOldNotifications() {
        try {
            System.out.println("Cleaning up old notifications...");
            ApiResponse<String> result = fileNotificationService.cleanupOldNotifications(30); // Cleanup 30+ days old

            if (result.success()) {
                System.out.println("Notification cleanup completed: " + result.message());
            } else {
                System.err.println("Notification cleanup failed: " + result.message());
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up notifications: " + e.getMessage());
        }
    }
}