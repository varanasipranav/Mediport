package com.appointment.webapp.repository;

import com.appointment.webapp.model.User;
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

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findAllByPhoneNumber(String phoneNumber);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByPatientId(String patientId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // Alias for consistency with controller usage
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRolesName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.enabled = true")
    List<User> findActiveUsersByRole(@Param("roleName") String roleName);

    List<User> findByEnabledTrue();

    List<User> findByEnabledFalse();

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword% OR u.username LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    // WhatsApp integration methods
    List<User> findByWhatsappPhone(String whatsappPhone);

    Optional<User> findFirstByWhatsappPhone(String whatsappPhone);

    boolean existsByWhatsappPhone(String whatsappPhone);

    // New methods for filtering and search
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE %:search% OR LOWER(u.lastName) LIKE %:search% OR LOWER(u.phoneNumber) LIKE %:search% OR LOWER(u.email) LIKE %:search%")
    List<User> findBySearchTerm(@Param("search") String search);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND (LOWER(u.firstName) LIKE %:search% OR LOWER(u.lastName) LIKE %:search% OR LOWER(u.phoneNumber) LIKE %:search% OR LOWER(u.email) LIKE %:search%)")
    List<User> findByRoleAndSearch(@Param("roleName") String roleName, @Param("search") String search);

    // Methods for ID generation
    @Query("SELECT COUNT(u) FROM User u WHERE u.employeeId LIKE CONCAT(:prefix, '%')")
    long countByEmployeeIdStartingWith(@Param("prefix") String prefix);

    @Query("SELECT COUNT(u) FROM User u WHERE u.patientId IS NOT NULL")
    long countByPatientIdNotNull();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    boolean existsByPatientId(String patientId);
}
