package com.kunal.gardengenius.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kunal.gardengenius.entity.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

	// Find an AuthToken by its refresh token
	Optional<AuthToken> findByRefreshToken(String refreshToken);

	// Find an AuthToken by its associated user ID
	Optional<AuthToken> findByUserId(Long userId);

	void delete(AuthToken authToken);

	@Modifying
	@Query("DELETE FROM AuthToken a WHERE a.expiryDate < :now")
	void deleteExpiredTokens(LocalDateTime now);
}
