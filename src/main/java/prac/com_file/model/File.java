package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "file")
@Audited
public class File extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "validity_date")
    private LocalDate validityDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    // CHANGED: Renamed to avoid conflict with BaseEntity.version
    @Column(name = "file_version", nullable = false)
    private String fileVersion = "1.0";

    @Column(name = "description")
    private String description;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    @JsonManagedReference
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_kar_user_id", nullable = false)
    @JsonManagedReference
    private User assignedKAR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @JsonManagedReference
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_partner_type_id", nullable = false)
    @JsonManagedReference
    private ChannelPartnerType channelPartnerType;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<FileHistory> fileHistories = new ArrayList<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<FileNotification> fileNotifications = new ArrayList<>();

    // Constructors
    public File() {}

    public File(String fileName, String fileUrl, LocalDate expiryDate, User uploadedBy, User assignedKAR, Region region, ChannelPartnerType channelPartnerType) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadDate = LocalDateTime.now();
        this.expiryDate = expiryDate;
        this.uploadedBy = uploadedBy;
        this.assignedKAR = assignedKAR;
        this.region = region;
        this.channelPartnerType = channelPartnerType;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDate validityDate) {
        this.validityDate = validityDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // CHANGED: Renamed getter/setter for file version
    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getAssignedKAR() {
        return assignedKAR;
    }

    public void setAssignedKAR(User assignedKAR) {
        this.assignedKAR = assignedKAR;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public ChannelPartnerType getChannelPartnerType() {
        return channelPartnerType;
    }

    public void setChannelPartnerType(ChannelPartnerType channelPartnerType) {
        this.channelPartnerType = channelPartnerType;
    }

    public List<FileHistory> getFileHistories() {
        return fileHistories;
    }

    public void setFileHistories(List<FileHistory> fileHistories) {
        this.fileHistories = fileHistories;
    }

    public List<FileNotification> getFileNotifications() {
        return fileNotifications;
    }

    public void setFileNotifications(List<FileNotification> fileNotifications) {
        this.fileNotifications = fileNotifications;
    }

    // Helper methods
    public void addFileHistory(FileHistory fileHistory) {
        fileHistories.add(fileHistory);
        fileHistory.setFile(this);
    }

    public void removeFileHistory(FileHistory fileHistory) {
        fileHistories.remove(fileHistory);
        fileHistory.setFile(null);
    }

    public void addFileNotification(FileNotification fileNotification) {
        fileNotifications.add(fileNotification);
        fileNotification.setFile(this);
    }

    public void removeFileNotification(FileNotification fileNotification) {
        fileNotifications.remove(fileNotification);
        fileNotification.setFile(null);
    }

    // Business logic methods
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return !isExpired() && (expiryDate.isEqual(thresholdDate) || expiryDate.isBefore(thresholdDate));
    }

    public boolean isValid() {
        if (validityDate == null) {
            return !isExpired();
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(validityDate) && !today.isAfter(expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileName, file.fileName) &&
                Objects.equals(fileUrl, file.fileUrl) &&
                Objects.equals(fileVersion, file.fileVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileUrl, fileVersion);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + getId() +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", expiryDate=" + expiryDate +
                ", fileVersion='" + fileVersion + '\'' +
                '}';
    }

    public static abstract class Meta {
        public static final String FILE_NAME = "fileName";
        public static final String FILE_URL = "fileUrl";
        public static final String UPLOAD_DATE = "uploadDate";
        public static final String VALIDITY_DATE = "validityDate";
        public static final String EXPIRY_DATE = "expiryDate";
        public static final String FILE_VERSION = "fileVersion"; // CHANGED
        public static final String UPLOADED_BY = "uploadedBy";
        public static final String ASSIGNED_KAR = "assignedKAR";
        public static final String REGION = "region";
        public static final String CHANNEL_PARTNER_TYPE = "channelPartnerType";
    }
}