package prac.com_file.dto;

import prac.com_file.model.Role;

/**
 * DTO for role responses.
 */
public class RoleResponseDto {
    private Long id;
    private String name;

    // No-args constructor
    public RoleResponseDto() {
    }

    // All-args constructor
    public RoleResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor from entity
    public RoleResponseDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
