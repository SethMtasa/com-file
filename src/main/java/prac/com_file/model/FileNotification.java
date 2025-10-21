package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "file_notification")
@Audited
public class FileNotification extends BaseEntity {

    public enum NotificationType {
        EXPIRY_REMINDER,
        EXPIRED,
        NEW_VERSION,
        ASSIGNMENT
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        READ,
        FAILED
    }

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "days_until_expiry")
    private Integer daysUntilExpiry;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @JsonManagedReference
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    @JsonManagedReference
    private User targetUser;

    // Constructors
    public FileNotification() {}

    public FileNotification(NotificationType notificationType, String title, String message,
                            LocalDateTime scheduledTime, File file, User targetUser) {
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.scheduledTime = scheduledTime;
        this.file = file;
        this.targetUser = targetUser;
    }

    // Getters and Setters
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public Integer getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(Integer daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    // Business logic methods
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentTime = LocalDateTime.now();
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
    }

    public boolean isPending() {
        return this.status == NotificationStatus.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileNotification that = (FileNotification) o;
        return notificationType == that.notificationType &&
                Objects.equals(title, that.title) &&
                Objects.equals(file, that.file) &&
                Objects.equals(targetUser, that.targetUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationType, title, file, targetUser);
    }

    @Override
    public String toString() {
        return "FileNotification{" +
                "id=" + getId() +
                ", notificationType=" + notificationType +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", scheduledTime=" + scheduledTime +
                '}';
    }

    public static abstract class Meta {
        public static final String NOTIFICATION_TYPE = "notificationType";
        public static final String TITLE = "title";
        public static final String STATUS = "status";
        public static final String SCHEDULED_TIME = "scheduledTime";
        public static final String FILE = "file";
        public static final String TARGET_USER = "targetUser";
    }
}