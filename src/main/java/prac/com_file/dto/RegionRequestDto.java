package prac.com_file.dto;

import jakarta.validation.constraints.NotBlank;

public class RegionRequestDto {

    @NotBlank(message = "Region name is required")
    private String regionName;

    @NotBlank(message = "Region code is required")
    private String regionCode;

    private String description;

    // Getters and Setters
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}