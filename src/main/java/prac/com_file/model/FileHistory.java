package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "file_history")
@Audited
public class FileHistory extends BaseEntity {

    // CHANGED: Renamed to avoid conflict
    @Column(name = "file_version", nullable = false)
    private String fileVersion;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "change_description")
    private String changeDescription;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @JsonManagedReference
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_user_id", nullable = false)
    @JsonManagedReference
    private User modifiedBy;

    // Constructors
    public FileHistory() {}

    public FileHistory(String fileVersion, String fileUrl, String fileName, LocalDateTime modifiedDate, File file, User modifiedBy) {
        this.fileVersion = fileVersion;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.modifiedDate = modifiedDate;
        this.file = file;
        this.modifiedBy = modifiedBy;
    }

    // Getters and Setters
    // CHANGED: Renamed getter/setter
    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileHistory that = (FileHistory) o;
        return Objects.equals(fileVersion, that.fileVersion) &&
                Objects.equals(fileUrl, that.fileUrl) &&
                Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileVersion, fileUrl, file);
    }

    @Override
    public String toString() {
        return "FileHistory{" +
                "id=" + getId() +
                ", fileVersion='" + fileVersion + '\'' +
                ", fileName='" + fileName + '\'' +
                ", modifiedDate=" + modifiedDate +
                '}';
    }

    public static abstract class Meta {
        public static final String FILE_VERSION = "fileVersion"; // CHANGED
        public static final String FILE_URL = "fileUrl";
        public static final String FILE_NAME = "fileName";
        public static final String MODIFIED_DATE = "modifiedDate";
        public static final String FILE = "file";
        public static final String MODIFIED_BY = "modifiedBy";
    }
}