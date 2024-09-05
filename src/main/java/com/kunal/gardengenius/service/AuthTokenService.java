package com.kunal.gardengenius.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.entity.AuthToken;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.AuthTokenRepository;

@Service
public class AuthTokenService {

	@Autowired
	private AuthTokenRepository authTokenRepository;

	@Value("${jwt.refreshExpirationMs}")
	private long refreshExpirationMs;

	public AuthToken saveAuthToken(AuthToken authToken) {

		// Set the expiry date as needed
		return authTokenRepository.save(authToken);
	}

	public Optional<AuthToken> findAuthTokenByRefreshToken(String refreshToken) {
		return authTokenRepository.findByRefreshToken(refreshToken);
	}

	public void deleteAuthTokenByUserId(Long userId) {
		authTokenRepository.deleteById(userId);
	}

	public AuthToken createAuthToken(User user, String refreshToken) {
		AuthToken authToken = new AuthToken();
		authToken.setUser(user);
		authToken.setRefreshToken(refreshToken);
		// Calculate expiration date
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryDate = now.plus(Duration.ofMillis(refreshExpirationMs));

		authToken.setExpiryDate(expiryDate);
		return authToken;
	}
	
	
	

	// Add additional methods as needed
}
