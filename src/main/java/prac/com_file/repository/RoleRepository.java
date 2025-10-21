package prac.com_file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prac.com_file.model.Role;
import java.util.Optional;

/**
 * Repository interface for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    @Query("SELECT ct FROM Role ct WHERE ct.name = :name")
    Optional<Role> findByName(@Param("name") String name);
}