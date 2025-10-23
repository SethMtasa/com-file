package prac.com_file.service;

import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileNotificationResponseDto;
import prac.com_file.model.File;
import prac.com_file.model.FileNotification;
import prac.com_file.model.User;

import java.util.List;

public interface FileNotificationService {

    ApiResponse<FileNotificationResponseDto> createNotification(File file, User targetUser,
                                                                FileNotification.NotificationType type,
                                                                String title, String message,
                                                                Integer daysUntilExpiry);

    void createExpiryNotification(File file, int daysUntilExpiry);

    ApiResponse<List<FileNotificationResponseDto>> getNotificationsByFileId(Long fileId);

    ApiResponse<List<FileNotificationResponseDto>> getNotificationsByUserId(Long userId);

    ApiResponse<List<FileNotificationResponseDto>> getActiveNotificationsByUser(Long userId);

    ApiResponse<List<FileNotificationResponseDto>> getNotificationsByStatus(String status);

    ApiResponse<List<FileNotificationResponseDto>> getPendingNotifications();

    ApiResponse<FileNotificationResponseDto> markNotificationAsRead(Long notificationId);

    ApiResponse<String> markAllNotificationsAsRead(Long userId);

    ApiResponse<FileNotificationResponseDto> getNotificationById(Long id);

    ApiResponse<String> deleteNotification(Long id);

    ApiResponse<Long> getUnreadNotificationCount(Long userId);

    ApiResponse<String> sendPendingNotifications();

    ApiResponse<List<FileNotificationResponseDto>> getExpiryRemindersByDays(Integer days);

    ApiResponse<String> cleanupOldNotifications(int daysOld);

    ApiResponse<List<FileNotificationResponseDto>> getAllNotifications();
}