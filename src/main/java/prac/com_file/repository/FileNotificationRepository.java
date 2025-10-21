package prac.com_file.repository;

import prac.com_file.model.FileNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileNotificationRepository extends JpaRepository<FileNotification, Long> {

    List<FileNotification> findByFileIdOrderByScheduledTimeDesc(Long fileId);

    List<FileNotification> findByTargetUserIdOrderByScheduledTimeDesc(Long targetUserId);

    List<FileNotification> findByStatusOrderByScheduledTimeDesc(FileNotification.NotificationStatus status);

    List<FileNotification> findByNotificationTypeOrderByScheduledTimeDesc(FileNotification.NotificationType notificationType);

    List<FileNotification> findByFileIdAndTargetUserIdOrderByScheduledTimeDesc(Long fileId, Long targetUserId);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.scheduledTime <= :currentTime AND fn.status = 'PENDING' ORDER BY fn.scheduledTime ASC")
    List<FileNotification> findPendingNotifications(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.targetUser.id = :userId AND fn.status = 'PENDING' ORDER BY fn.scheduledTime ASC")
    List<FileNotification> findPendingNotificationsByUser(@Param("userId") Long userId);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.file.id = :fileId AND fn.notificationType = 'EXPIRY_REMINDER' ORDER BY fn.scheduledTime DESC")
    List<FileNotification> findExpiryRemindersByFileId(@Param("fileId") Long fileId);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.scheduledTime BETWEEN :startDate AND :endDate ORDER BY fn.scheduledTime DESC")
    List<FileNotification> findByScheduledTimeBetween(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.sentTime BETWEEN :startDate AND :endDate ORDER BY fn.sentTime DESC")
    List<FileNotification> findBySentTimeBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(fn) FROM FileNotification fn WHERE fn.status = :status")
    long countByStatus(@Param("status") FileNotification.NotificationStatus status);

    @Query("SELECT COUNT(fn) FROM FileNotification fn WHERE fn.targetUser.id = :userId AND fn.status = 'UNREAD'")
    long countUnreadByUser(@Param("userId") Long userId);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.targetUser.id = :userId AND fn.status IN ('SENT', 'PENDING') ORDER BY fn.scheduledTime DESC")
    List<FileNotification> findActiveNotificationsByUser(@Param("userId") Long userId);

    @Query("SELECT fn FROM FileNotification fn WHERE fn.daysUntilExpiry = :days AND fn.notificationType = 'EXPIRY_REMINDER'")
    List<FileNotification> findExpiryRemindersByDays(@Param("days") Integer days);

    // ADD THIS MISSING METHOD
    @Query("SELECT fn FROM FileNotification fn WHERE fn.scheduledTime < :cutoffDate AND fn.activeStatus = true")
    List<FileNotification> findByScheduledTimeBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}