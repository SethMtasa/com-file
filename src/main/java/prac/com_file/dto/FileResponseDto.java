package prac.com_file.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileResponseDto {

    private Long id;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadDate;
    private LocalDate validityDate;
    private LocalDate expiryDate;
    private String fileVersion;
    private String description;
    private boolean expired;
    private boolean valid;
    private Long version;

    // Nested DTOs for related entities
    private UserResponseDto uploadedBy;
    private UserResponseDto assignedKAR;
    private RegionResponseDto region;
    private ChannelPartnerTypeResponseDto channelPartnerType;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public UserResponseDto getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserResponseDto uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public UserResponseDto getAssignedKAR() {
        return assignedKAR;
    }

    public void setAssignedKAR(UserResponseDto assignedKAR) {
        this.assignedKAR = assignedKAR;
    }

    public RegionResponseDto getRegion() {
        return region;
    }

    public void setRegion(RegionResponseDto region) {
        this.region = region;
    }

    public ChannelPartnerTypeResponseDto getChannelPartnerType() {
        return channelPartnerType;
    }

    public void setChannelPartnerType(ChannelPartnerTypeResponseDto channelPartnerType) {
        this.channelPartnerType = channelPartnerType;
    }
}