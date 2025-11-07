package com.hoxuanthai.be.lastdance.repository;

import com.hoxuanthai.be.lastdance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on November 2025
 *
 * @author HoXuanThai
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

}
