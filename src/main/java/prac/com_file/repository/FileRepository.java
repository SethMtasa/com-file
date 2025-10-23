package prac.com_file.repository;

import prac.com_file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByAssignedKARIdAndActiveStatus(Long assignedKARId, boolean activeStatus);

    List<File> findByRegionIdAndActiveStatus(Long regionId, boolean activeStatus);

    List<File> findByChannelPartnerTypeIdAndActiveStatus(Long channelPartnerTypeId, boolean activeStatus);

    List<File> findByUploadedByIdAndActiveStatus(Long uploadedById, boolean activeStatus);

    List<File> findByActiveStatus(boolean activeStatus);

    @Query("SELECT f FROM File f WHERE f.expiryDate <= :thresholdDate AND f.expiryDate >= CURRENT_DATE AND f.activeStatus = true")
    List<File> findFilesExpiringInDays(@Param("thresholdDate") LocalDate thresholdDate);

    default List<File> findFilesExpiringInDays(int days) {
        LocalDate thresholdDate = LocalDate.now().plusDays(days);
        return findFilesExpiringInDays(thresholdDate);
    }

    @Query("SELECT f FROM File f WHERE f.expiryDate < CURRENT_DATE AND f.activeStatus = true")
    List<File> findExpiredFiles();

    @Query("SELECT f FROM File f WHERE f.validityDate > CURRENT_DATE AND f.activeStatus = true")
    List<File> findFilesNotYetValid();

    @Query("SELECT f FROM File f WHERE f.validityDate <= CURRENT_DATE AND f.expiryDate >= CURRENT_DATE AND f.activeStatus = true")
    List<File> findCurrentlyValidFiles();

    @Query("SELECT f FROM File f WHERE " +
            "(:fileName IS NULL OR LOWER(f.fileName) LIKE LOWER(CONCAT('%', :fileName, '%'))) AND " +
            "(:regionId IS NULL OR f.region.id = :regionId) AND " +
            "(:typeId IS NULL OR f.channelPartnerType.id = :typeId) AND " +
            "(:karUserId IS NULL OR f.assignedKAR.id = :karUserId) AND " +
            "f.activeStatus = true")
    List<File> searchFiles(@Param("fileName") String fileName,
                           @Param("regionId") Long regionId,
                           @Param("typeId") Long typeId,
                           @Param("karUserId") Long karUserId);

    @Query("SELECT f FROM File f WHERE LOWER(f.fileName) LIKE LOWER(CONCAT('%', :fileName, '%')) AND f.activeStatus = true")
    List<File> findByFileNameContainingIgnoreCase(@Param("fileName") String fileName);

    @Query("SELECT COUNT(f) FROM File f WHERE f.assignedKAR.id = :karUserId AND f.activeStatus = true")
    long countByAssignedKAR(@Param("karUserId") Long karUserId);

    @Query("SELECT COUNT(f) FROM File f WHERE f.region.id = :regionId AND f.activeStatus = true")
    long countByRegion(@Param("regionId") Long regionId);

    @Query("SELECT COUNT(f) FROM File f WHERE f.channelPartnerType.id = :typeId AND f.activeStatus = true")
    long countByChannelPartnerType(@Param("typeId") Long typeId);

    @Query("SELECT COUNT(f) FROM File f WHERE f.expiryDate <= :date AND f.activeStatus = true")
    long countExpiredFilesByDate(@Param("date") LocalDate date);

    @Query("SELECT f FROM File f WHERE f.assignedKAR.id = :karUserId AND f.expiryDate BETWEEN CURRENT_DATE AND :futureDate AND f.activeStatus = true")
    List<File> findExpiringFilesByKAR(@Param("karUserId") Long karUserId, @Param("futureDate") LocalDate futureDate);

    @Query("SELECT f FROM File f WHERE f.fileVersion = :version AND f.activeStatus = true")
    List<File> findByFileVersion(@Param("version") String version);

    @Query("SELECT DISTINCT f.fileType FROM File f WHERE f.activeStatus = true")
    List<String> findDistinctFileTypes();


        @Query("SELECT f FROM File f WHERE " +
                "(f.assignedKAR.id = :karUserId OR f.uploadedBy.id = :karUserId) AND " +
                "f.activeStatus = true " +
                "ORDER BY f.uploadDate DESC")
        List<File> findFilesByAssignedKAROrUploadedBy(@Param("karUserId") Long karUserId);


}