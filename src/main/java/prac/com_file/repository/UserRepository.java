package prac.com_file.repository;

import prac.com_file.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndActiveStatus(String username, boolean activeStatus);

    List<User> findByActiveStatus(boolean activeStatus);

    List<User> findByRoleName(String roleName);

    @Query("SELECT u FROM User u WHERE u.role.name = 'KAR' AND u.activeStatus = true")
    List<User> findAllActiveKARs();

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) AND u.activeStatus = true")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.activeStatus = true")
    List<User> findByRoleNameAndActiveStatus(@Param("roleName") String roleName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.activeStatus = true AND u.role.name = 'KAR'")
    long countActiveKARs();

    @Query("SELECT u FROM User u WHERE u.id IN (SELECT DISTINCT f.assignedKAR.id FROM File f WHERE f.activeStatus = true)")
    List<User> findUsersWithAssignedFiles();
}