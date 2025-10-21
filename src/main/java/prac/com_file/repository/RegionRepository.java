package prac.com_file.repository;

import prac.com_file.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByRegionName(String regionName);

    Optional<Region> findByRegionCode(String regionCode);

    List<Region> findByActiveStatus(boolean activeStatus);

    @Query("SELECT r FROM Region r WHERE LOWER(r.regionName) LIKE LOWER(CONCAT('%', :name, '%')) AND r.activeStatus = true")
    List<Region> findByRegionNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT r FROM Region r WHERE LOWER(r.regionCode) LIKE LOWER(CONCAT('%', :code, '%')) AND r.activeStatus = true")
    List<Region> findByRegionCodeContainingIgnoreCase(@Param("code") String code);

    boolean existsByRegionCode(String regionCode);

    boolean existsByRegionName(String regionName);

    @Query("SELECT COUNT(r) FROM Region r WHERE r.activeStatus = true")
    long countActiveRegions();
}