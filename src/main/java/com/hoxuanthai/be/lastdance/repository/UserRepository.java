package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.devices WHERE u.username = ?1")
	User findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.devices WHERE u.id = ?1 AND u.userRole = 'USER'")
	Optional<User> findByIdWithDevices(Long id);

	@Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.devices WHERE u.userRole = 'USER'",
		   countQuery = "SELECT COUNT(u) FROM User u WHERE u.userRole = 'USER'")
	Page<User> findAllWithDevices(Pageable pageable);

	@Query("SELECT COUNT(u.id) FROM User u WHERE u.userRole = 'USER'")
	Long countUser();

	@Query("SELECT COUNT(u.id) FROM User u WHERE u.userRole = 'USER' AND u.enabled = true")
	Long countUserActive();
}
