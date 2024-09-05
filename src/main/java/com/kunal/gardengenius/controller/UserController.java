package com.kunal.gardengenius.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.AuthRequest;
import com.kunal.gardengenius.DTO.AuthResponse;
import com.kunal.gardengenius.DTO.LogoutRequest;
import com.kunal.gardengenius.DTO.RefreshTokenRequest;
import com.kunal.gardengenius.DTO.UserDTO;
import com.kunal.gardengenius.entity.AuthToken;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;
import com.kunal.gardengenius.service.AuthTokenService;
import com.kunal.gardengenius.service.JwtUtils;
import com.kunal.gardengenius.service.RefreshTokenService;
import com.kunal.gardengenius.service.TokenBlacklistService;
import com.kunal.gardengenius.service.UserInfoDetails;
import com.kunal.gardengenius.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private UserRepository repository;

	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> authenticate(@ModelAttribute AuthRequest authRequest) {
		// Authenticate the user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

		// Set the authentication in the security context
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Generate JWT tokens
		String accessToken = jwtUtils.generateAccessToken(authentication);
		String refreshToken = jwtUtils.generateRefreshToken(authentication);

		// Retrieve the User from UserInfoDetails
		UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
		User user = userInfoDetails.getUser();

		// Save the refresh token in the database

		AuthToken authToken = authTokenService.createAuthToken(user, refreshToken); // Save the AuthToken entity

		authTokenService.saveAuthToken(authToken);
		// Return the response with tokens
		AuthResponse authResponse = new AuthResponse(accessToken, refreshToken);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {

		// Extract the refresh token from the request
		String refreshToken = request.getRefreshToken();

		// Check if the refresh token is null
		if (refreshToken == null) {
			return ResponseEntity.badRequest().body(null); // or return a custom error response
		}

		// Validate the refresh token
		if (!refreshTokenService.validateRefreshToken(refreshToken)) {
			return ResponseEntity.badRequest().body(null); // or return a custom error response
		}

		// Extract the username from the valid refresh token
		String username = refreshTokenService.getUserNameFromRefreshToken(refreshToken);

		// Generate a new access token and a new refresh token
		String newAccessToken = refreshTokenService.createNewAccessToken(username);
		String newRefreshToken = refreshTokenService.createNewRefreshToken(username);

		// Replace the old refresh token with the new one in the database
		refreshTokenService.replaceRefreshToken(refreshToken, newRefreshToken);

		// Return the new tokens in the response
		return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@ModelAttribute User user) {

		try {
			Optional<User> existingUser = repository.findByEmail(user.getEmail());
			if (existingUser.isPresent()) {
				throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
			}

			// Proceed with the registration logic
			service.addUser(user);
			return ResponseEntity.ok("User registered successfully!");
		} catch (IllegalArgumentException e) {
			// Return a user-friendly response
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
		String accessToken = logoutRequest.getAccessToken();
		String refreshToken = logoutRequest.getRefreshToken();

		// Validate and remove the refresh token
		if (refreshToken == null || refreshToken.isEmpty()) {
			return ResponseEntity.badRequest().body("Refresh token is missing");
		}

		// Validate the access token
		if (accessToken == null || accessToken.isEmpty() || !jwtUtils.validateAccessToken(accessToken)) {
			return ResponseEntity.badRequest().body("Access token is invalid or missing");
		}

		boolean isRefreshTokenRemoved = refreshTokenService.removeRefreshToken(refreshToken);

		if (!isRefreshTokenRemoved) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
		}

		// Clear the security context
		SecurityContextHolder.clearContext();

		// Invalidate the access token by adding it to the blacklist
		boolean isAccessTokenBlacklisted = tokenBlacklistService.blacklistToken(accessToken);

		if (!isAccessTokenBlacklisted) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to blacklist access token");
		}

		// Clear the security context
		SecurityContextHolder.clearContext();

		// Return a success response
		return ResponseEntity.ok("Logout successful");
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getUserHome() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication != null && authentication.getPrincipal() instanceof UserInfoDetails) {
				UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
				User user = userInfoDetails.getUser();

				UserDTO userDTO = new UserDTO();
				userDTO.setId(user.getId());
				userDTO.setFirstName(user.getFirstName());
				userDTO.setLastName(user.getLastName());
				userDTO.setUsername(user.getUsername());
				userDTO.setEmail(user.getEmail());
				userDTO.setPhoneNumber(user.getPhoneNumber());
				userDTO.setAddress(user.getAddress());

				return ResponseEntity.ok(userDTO);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user information");
		}

	}

}
