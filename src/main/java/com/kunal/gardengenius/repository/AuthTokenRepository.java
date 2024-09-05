package com.kunal.gardengenius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kunal.gardengenius.entity.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

	// Find an AuthToken by its refresh token
	Optional<AuthToken> findByRefreshToken(String refreshToken);

	// Find an AuthToken by its associated user ID
	Optional<AuthToken> findByUserId(Long userId);

	void delete(AuthToken authToken);

	// Optionally, you could add a method to delete expired tokens
	// Uncomment if needed
	// @Modifying
	// @Query("DELETE FROM AuthToken a WHERE a.expiryDate < CURRENT_TIMESTAMP")
	// void deleteExpiredTokens();
}
