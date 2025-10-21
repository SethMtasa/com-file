package prac.com_file.repository;

import prac.com_file.model.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {

    List<FileHistory> findByFileIdOrderByModifiedDateDesc(Long fileId);

    List<FileHistory> findByFileIdAndActiveStatusOrderByModifiedDateDesc(Long fileId, boolean activeStatus);

    List<FileHistory> findByModifiedByIdOrderByModifiedDateDesc(Long userId);

    List<FileHistory> findByFileId(Long fileId);

    List<FileHistory> findAllByOrderByModifiedDateDesc();

    int countByFileIdAndActiveStatus(Long fileId, boolean activeStatus);

    @Query("SELECT fh FROM FileHistory fh WHERE fh.file.id = :fileId AND fh.fileVersion != :currentVersion ORDER BY fh.modifiedDate DESC")
    Optional<FileHistory> findPreviousVersion(@Param("fileId") Long fileId, @Param("currentVersion") String currentVersion);

    @Query("SELECT fh FROM FileHistory fh WHERE fh.file.id = :fileId AND fh.fileVersion = :version")
    Optional<FileHistory> findByFileIdAndVersion(@Param("fileId") Long fileId, @Param("version") String version);

    @Query("SELECT fh FROM FileHistory fh WHERE fh.modifiedBy.id = :userId AND fh.activeStatus = true ORDER BY fh.modifiedDate DESC")
    List<FileHistory> findByModifiedByAndActiveStatus(@Param("userId") Long userId);

    @Query("SELECT fh FROM FileHistory fh WHERE fh.file.id = :fileId AND fh.activeStatus = true ORDER BY fh.modifiedDate DESC LIMIT 1")
    Optional<FileHistory> findLatestByFileId(@Param("fileId") Long fileId);

    @Query("SELECT COUNT(fh) FROM FileHistory fh WHERE fh.file.id = :fileId AND fh.activeStatus = true")
    long countActiveHistoryByFileId(@Param("fileId") Long fileId);

    @Query("SELECT fh FROM FileHistory fh WHERE fh.modifiedDate BETWEEN :startDate AND :endDate AND fh.activeStatus = true ORDER BY fh.modifiedDate DESC")
    List<FileHistory> findByModifiedDateBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                                @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT DISTINCT fh.fileVersion FROM FileHistory fh WHERE fh.file.id = :fileId AND fh.activeStatus = true ORDER BY fh.fileVersion DESC")
    List<String> findDistinctVersionsByFileId(@Param("fileId") Long fileId);
}