package prac.com_file.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "region")
@Audited
public class Region extends BaseEntity {

    @Column(name = "region_name", nullable = false, unique = true)
    private String regionName;

    @Column(name = "region_code", nullable = false, unique = true)
    private String regionCode;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<File> files = new ArrayList<>();

    // Constructors
    public Region() {}

    public Region(String regionName, String regionCode, String description) {
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.description = description;
    }

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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    // Helper methods
    public void addFile(File file) {
        files.add(file);
        file.setRegion(this);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.setRegion(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return Objects.equals(regionName, region.regionName) &&
                Objects.equals(regionCode, region.regionCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionName, regionCode);
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + getId() +
                ", regionName='" + regionName + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static abstract class Meta {
        public static final String REGION_NAME = "regionName";
        public static final String REGION_CODE = "regionCode";
        public static final String DESCRIPTION = "description";
    }
}