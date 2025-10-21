package prac.com_file.dto;

import prac.com_file.model.User;

/**
 * DTO for user responses.
 */
public class UserResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private RoleResponseDto role;

    // No-args constructor
    public UserResponseDto() {
    }

    // All-args constructor
    public UserResponseDto(Long id, String username, String firstName, String lastName, String email, RoleResponseDto role) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    // Constructor from entity
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        // Assuming you have a RoleResponseDto, which is necessary to avoid recursion.
        this.role = new RoleResponseDto(user.getRole());
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public RoleResponseDto getRole() {
        return role;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(RoleResponseDto role) {
        this.role = role;
    }
}
