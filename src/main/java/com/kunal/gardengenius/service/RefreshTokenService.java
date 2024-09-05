package com.kunal.gardengenius.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.entity.AuthToken;
import com.kunal.gardengenius.repository.AuthTokenRepository;

@Service
public class RefreshTokenService {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthTokenRepository authTokenRepository;

	@Value("${jwt.refreshExpirationMs}")
	private int jwtRefreshExpirationMs;

	// Generates a new refresh token
	public String createNewAccessToken(String username) {
		return jwtUtils.generateAccessToken(username);
	}

	public String createNewRefreshToken(String username) {
		return jwtUtils.generateRefreshToken(username);
	}

	// Validates a refresh token
	public boolean validateRefreshToken(String refreshToken) {
		// First, validate the token structure and signature
		if (!jwtUtils.validatRefreshToken(refreshToken)) {
			return false;
		}

		// Check if the token exists in the database and is not expired
		Optional<AuthToken> authTokenOptional = authTokenRepository.findByRefreshToken(refreshToken);

		if (authTokenOptional.isPresent()) {
			AuthToken authToken = authTokenOptional.get();
			// Check if the token is expired
			return authToken.getExpiryDate().isAfter(LocalDateTime.now());
		}

		// Token not found in database or has expired
		return false;
	}

	// Gets username from refresh token
	public String getUserNameFromRefreshToken(String token) {
		return jwtUtils.getUserNameFromRefreshToken(token);
	}

	// Method to replace the old refresh token with a new one
	public void replaceRefreshToken(String oldRefreshToken, String newRefreshToken) {
		// Find the AuthToken with the old refresh token
		Optional<AuthToken> authTokenOptional = authTokenRepository.findByRefreshToken(oldRefreshToken);

		if (authTokenOptional.isPresent()) {
			AuthToken authToken = authTokenOptional.get();
			authToken.setRefreshToken(newRefreshToken);

			// Set the new expiration date
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expiryDate = now.plus(Duration.ofMillis(jwtRefreshExpirationMs));
			authToken.setExpiryDate(expiryDate);

			// Save the updated AuthToken
			authTokenRepository.save(authToken);
		} else {
			throw new RuntimeException("Refresh Token not found"); // Handle token not found
		}
	}

	public boolean removeRefreshToken(String refreshToken) {
		Optional<AuthToken> optionalAuthToken = authTokenRepository.findByRefreshToken(refreshToken);

		if (optionalAuthToken.isPresent()) {
			AuthToken authToken = optionalAuthToken.get();
			authTokenRepository.delete(authToken);
			return true;
		}

		return false;
	}
}
