package prac.com_file.dto;

import java.time.LocalDateTime;

public class FileNotificationResponseDto {

    private Long id;
    private String notificationType;
    private String title;
    private String message;
    private LocalDateTime scheduledTime;
    private LocalDateTime sentTime;
    private String status;
    private Integer daysUntilExpiry;

    private FileResponseDto file;
    private UserResponseDto targetUser;

    // Constructors
    public FileNotificationResponseDto() {
    }

    public FileNotificationResponseDto(Long id, String notificationType, String title, String message,
                                       LocalDateTime scheduledTime, LocalDateTime sentTime, String status,
                                       Integer daysUntilExpiry) {
        this.id = id;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.scheduledTime = scheduledTime;
        this.sentTime = sentTime;
        this.status = status;
        this.daysUntilExpiry = daysUntilExpiry;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(Integer daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public FileResponseDto getFile() {
        return file;
    }

    public void setFile(FileResponseDto file) {
        this.file = file;
    }

    public UserResponseDto getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(UserResponseDto targetUser) {
        this.targetUser = targetUser;
    }

    @Override
    public String toString() {
        return "FileNotificationResponseDto{" +
                "id=" + id +
                ", notificationType='" + notificationType + '\'' +
                ", title='" + title + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", status='" + status + '\'' +
                ", daysUntilExpiry=" + daysUntilExpiry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileNotificationResponseDto that = (FileNotificationResponseDto) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}