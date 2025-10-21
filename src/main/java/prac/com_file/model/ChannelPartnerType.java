package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "channel_partner_type")
@Audited
public class ChannelPartnerType extends BaseEntity {

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "channelPartnerType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<File> files = new ArrayList<>();

    // Constructors
    public ChannelPartnerType() {}

    public ChannelPartnerType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
    }

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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    // Helper methods
    public void addFile(File file) {
        files.add(file);
        file.setChannelPartnerType(this);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.setChannelPartnerType(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelPartnerType that = (ChannelPartnerType) o;
        return Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }

    @Override
    public String toString() {
        return "ChannelPartnerType{" +
                "id=" + getId() +
                ", typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static abstract class Meta {
        public static final String TYPE_NAME = "typeName";
        public static final String DESCRIPTION = "description";
    }
}