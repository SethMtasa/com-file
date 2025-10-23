package prac.com_file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class FileRequestDto {

    // This field will be handled by @RequestParam MultipartFile
    private transient MultipartFile file;

    @NotBlank(message = "File name is required")
    private String fileName;

    private String description;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private LocalDate validityDate;

    @NotNull(message = "Region ID is required")
    private Long regionId;

    @NotNull(message = "Channel partner type ID is required")
    private Long channelPartnerTypeId;

    @NotNull(message = "Assigned KAR user ID is required")
    private Long assignedKarUserId;

    // NEW: Comment field for email notification (not saved in DB)
    @NotBlank(message = "Comment is required for the assigned KAR")
    private String comment;

    // Getters and Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDate validityDate) {
        this.validityDate = validityDate;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getChannelPartnerTypeId() {
        return channelPartnerTypeId;
    }

    public void setChannelPartnerTypeId(Long channelPartnerTypeId) {
        this.channelPartnerTypeId = channelPartnerTypeId;
    }

    public Long getAssignedKarUserId() {
        return assignedKarUserId;
    }

    public void setAssignedKarUserId(Long assignedKarUserId) {
        this.assignedKarUserId = assignedKarUserId;
    }

    // NEW: Getter and setter for comment
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}