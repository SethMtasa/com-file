package prac.com_file.repository;

import prac.com_file.model.ChannelPartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelPartnerTypeRepository extends JpaRepository<ChannelPartnerType, Long> {

    Optional<ChannelPartnerType> findByTypeName(String typeName);

    List<ChannelPartnerType> findByActiveStatus(boolean activeStatus);

    @Query("SELECT c FROM ChannelPartnerType c WHERE LOWER(c.typeName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.activeStatus = true")
    List<ChannelPartnerType> findByTypeNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByTypeName(String typeName);

    @Query("SELECT COUNT(c) FROM ChannelPartnerType c WHERE c.activeStatus = true")
    long countActiveTypes();
}