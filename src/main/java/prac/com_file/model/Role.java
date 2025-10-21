package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "active_status")
    private boolean activeStatus;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference // Indicates the "parent" in the relationship
    private List<User> users;


    public Role() {
        // Default constructor for JPA
    }

    // Getters and setters
    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public boolean getActiveStatus() {
        return activeStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    // Manual implementation of equals() and hashCode() to replace Lombok annotation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // This handles the callSuper = true part
        Role role = (Role) o;
        return activeStatus == role.activeStatus &&
                Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, activeStatus);
    }
}
