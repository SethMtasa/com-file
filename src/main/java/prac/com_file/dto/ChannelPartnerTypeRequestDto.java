package prac.com_file.dto;

import jakarta.validation.constraints.NotBlank;

public class ChannelPartnerTypeRequestDto {

    @NotBlank(message = "Type name is required")
    private String typeName;

    private String description;

    // Getters and Setters
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}