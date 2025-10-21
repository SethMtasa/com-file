package prac.com_file.dto;

import java.time.LocalDateTime;

public class FileHistoryResponseDto {

    private Long id;
    private String fileVersion;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private LocalDateTime modifiedDate;
    private String changeDescription;

    private UserResponseDto modifiedBy;
    private FileResponseDto file;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UserResponseDto getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(UserResponseDto modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public FileResponseDto getFile() {
        return file;
    }

    public void setFile(FileResponseDto file) {
        this.file = file;
    }
}