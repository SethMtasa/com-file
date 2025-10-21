package prac.com_file.service;

import org.springframework.stereotype.Service;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileNotificationResponseDto;
import prac.com_file.model.File;
import prac.com_file.model.FileNotification;
import prac.com_file.model.User;
import prac.com_file.repository.FileNotificationRepository;
import prac.com_file.repository.FileRepository;
import prac.com_file.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileNotificationServiceImpl implements FileNotificationService {

    private final FileNotificationRepository fileNotificationRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // Assuming you have an email service

    public FileNotificationServiceImpl(FileNotificationRepository fileNotificationRepository,
                                       FileRepository fileRepository,
                                       UserRepository userRepository,
                                       EmailService emailService) {
        this.fileNotificationRepository = fileNotificationRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public ApiResponse<FileNotificationResponseDto> createNotification(File file, User targetUser,
                                                                       FileNotification.NotificationType type,
                                                                       String title, String message,
                                                                       Integer daysUntilExpiry) {
        try {
            FileNotification notification = new FileNotification();
            notification.setNotificationType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setScheduledTime(LocalDateTime.now());
            notification.setStatus(FileNotification.NotificationStatus.PENDING);
            notification.setDaysUntilExpiry(daysUntilExpiry);
            notification.setFile(file);
            notification.setTargetUser(targetUser);

            FileNotification savedNotification = fileNotificationRepository.save(notification);
            FileNotificationResponseDto responseDto = convertToDto(savedNotification);

            return new ApiResponse<>(true, "Notification created successfully", responseDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create notification: " + e.getMessage(), null);
        }
    }

    @Override
    public void createExpiryNotification(File file, int daysUntilExpiry) {
        try {
            String title = "File Expiry Reminder";
            String message = String.format("The file '%s' will expire in %d days. Please review and take necessary action.",
                    file.getFileName(), daysUntilExpiry);

            FileNotification notification = new FileNotification();
            notification.setNotificationType(FileNotification.NotificationType.EXPIRY_REMINDER);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setScheduledTime(LocalDateTime.now());
            notification.setStatus(FileNotification.NotificationStatus.PENDING);
            notification.setDaysUntilExpiry(daysUntilExpiry);
            notification.setFile(file);
            notification.setTargetUser(file.getAssignedKAR());

            fileNotificationRepository.save(notification);
        } catch (Exception e) {
            // Log error but don't throw to avoid interrupting the main process
            System.err.println("Failed to create expiry notification: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getNotificationsByFileId(Long fileId) {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findByFileIdOrderByScheduledTimeDesc(fileId);
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Notifications retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No notifications found for file ID: " + fileId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getNotificationsByUserId(Long userId) {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findByTargetUserIdOrderByScheduledTimeDesc(userId);
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Notifications retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No notifications found for user ID: " + userId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getActiveNotificationsByUser(Long userId) {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findActiveNotificationsByUser(userId);
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Active notifications retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No active notifications found for user ID: " + userId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve active notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getNotificationsByStatus(String status) {
        try {
            FileNotification.NotificationStatus notificationStatus =
                    FileNotification.NotificationStatus.valueOf(status.toUpperCase());

            List<FileNotification> notifications = fileNotificationRepository.findByStatusOrderByScheduledTimeDesc(notificationStatus);
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Notifications retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No notifications found with status: " + status, null);
            }
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, "Invalid status: " + status, null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getPendingNotifications() {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findPendingNotifications(LocalDateTime.now());
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Pending notifications retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No pending notifications found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve pending notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<FileNotificationResponseDto> markNotificationAsRead(Long notificationId) {
        try {
            Optional<FileNotification> notificationOptional = fileNotificationRepository.findById(notificationId);
            if (notificationOptional.isPresent()) {
                FileNotification notification = notificationOptional.get();
                notification.setStatus(FileNotification.NotificationStatus.READ);
                FileNotification updatedNotification = fileNotificationRepository.save(notification);
                return new ApiResponse<>(true, "Notification marked as read", convertToDto(updatedNotification));
            } else {
                return new ApiResponse<>(false, "Notification not found with ID: " + notificationId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to mark notification as read: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> markAllNotificationsAsRead(Long userId) {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findByTargetUserIdOrderByScheduledTimeDesc(userId);
            List<FileNotification> unreadNotifications = notifications.stream()
                    .filter(n -> n.getStatus() == FileNotification.NotificationStatus.SENT)
                    .collect(Collectors.toList());

            if (!unreadNotifications.isEmpty()) {
                unreadNotifications.forEach(n -> n.setStatus(FileNotification.NotificationStatus.READ));
                fileNotificationRepository.saveAll(unreadNotifications);
                return new ApiResponse<>(true, "All notifications marked as read", null);
            } else {
                return new ApiResponse<>(false, "No unread notifications found for user ID: " + userId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to mark all notifications as read: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<FileNotificationResponseDto> getNotificationById(Long id) {
        try {
            Optional<FileNotification> notification = fileNotificationRepository.findById(id);
            return notification.map(n -> new ApiResponse<>(true, "Notification found", convertToDto(n)))
                    .orElseGet(() -> new ApiResponse<>(false, "Notification not found with ID: " + id, null));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve notification: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> deleteNotification(Long id) {
        try {
            Optional<FileNotification> notificationOptional = fileNotificationRepository.findById(id);
            if (notificationOptional.isPresent()) {
                FileNotification notification = notificationOptional.get();
                notification.setActiveStatus(false);
                fileNotificationRepository.save(notification);
                return new ApiResponse<>(true, "Notification deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "Notification not found with ID: " + id, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete notification: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<Long> getUnreadNotificationCount(Long userId) {
        try {
            long count = fileNotificationRepository.countUnreadByUser(userId);
            return new ApiResponse<>(true, "Unread notification count retrieved", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to get unread notification count: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> sendPendingNotifications() {
        try {
            List<FileNotification> pendingNotifications = fileNotificationRepository.findPendingNotifications(LocalDateTime.now());
            int sentCount = 0;
            int failedCount = 0;

            for (FileNotification notification : pendingNotifications) {
                try {
                    // Send email notification
                    boolean emailSent = emailService.sendNotificationEmail(
                            notification.getTargetUser().getEmail(),
                            notification.getTitle(),
                            notification.getMessage()
                    );

                    if (emailSent) {
                        notification.markAsSent();
                        fileNotificationRepository.save(notification);
                        sentCount++;
                    } else {
                        notification.setStatus(FileNotification.NotificationStatus.FAILED);
                        fileNotificationRepository.save(notification);
                        failedCount++;
                    }
                } catch (Exception e) {
                    notification.setStatus(FileNotification.NotificationStatus.FAILED);
                    fileNotificationRepository.save(notification);
                    failedCount++;
                    System.err.println("Failed to send notification " + notification.getId() + ": " + e.getMessage());
                }
            }

            String message = String.format("Notification sending completed. Sent: %d, Failed: %d", sentCount, failedCount);
            return new ApiResponse<>(true, message, null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to send pending notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileNotificationResponseDto>> getExpiryRemindersByDays(Integer days) {
        try {
            List<FileNotification> notifications = fileNotificationRepository.findExpiryRemindersByDays(days);
            if (!notifications.isEmpty()) {
                List<FileNotificationResponseDto> notificationDtos = notifications.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "Expiry reminders retrieved successfully", notificationDtos);
            } else {
                return new ApiResponse<>(false, "No expiry reminders found for " + days + " days", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve expiry reminders: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> cleanupOldNotifications(int daysOld) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            List<FileNotification> oldNotifications = fileNotificationRepository.findByScheduledTimeBefore(cutoffDate);

            if (!oldNotifications.isEmpty()) {
                oldNotifications.forEach(notification -> notification.setActiveStatus(false));
                fileNotificationRepository.saveAll(oldNotifications);
                return new ApiResponse<>(true, "Cleaned up " + oldNotifications.size() + " old notifications", null);
            } else {
                return new ApiResponse<>(false, "No old notifications found to cleanup", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to cleanup old notifications: " + e.getMessage(), null);
        }
    }

    /**
     * Converts FileNotification entity to FileNotificationResponseDto
     */
    private FileNotificationResponseDto convertToDto(FileNotification notification) {
        FileNotificationResponseDto dto = new FileNotificationResponseDto();
        dto.setId(notification.getId());
        dto.setNotificationType(notification.getNotificationType().toString());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setScheduledTime(notification.getScheduledTime());
        dto.setSentTime(notification.getSentTime());
        dto.setStatus(notification.getStatus().toString());
        dto.setDaysUntilExpiry(notification.getDaysUntilExpiry());

        // Convert nested entities to DTOs
        if (notification.getFile() != null) {
            // Create minimal file DTO to avoid circular references
            prac.com_file.dto.FileResponseDto fileDto = new prac.com_file.dto.FileResponseDto();
            fileDto.setId(notification.getFile().getId());
            fileDto.setFileName(notification.getFile().getFileName());
            fileDto.setFileVersion(notification.getFile().getFileVersion());
            fileDto.setExpiryDate(notification.getFile().getExpiryDate());
            dto.setFile(fileDto);
        }

        if (notification.getTargetUser() != null) {
            prac.com_file.dto.UserResponseDto userDto = new prac.com_file.dto.UserResponseDto(notification.getTargetUser());
            dto.setTargetUser(userDto);
        }

        return dto;
    }
}