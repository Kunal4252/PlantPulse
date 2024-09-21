package com.kunal.gardengenius.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kunal.gardengenius.DTO.AuthRequest;
import com.kunal.gardengenius.DTO.AuthResponse;
import com.kunal.gardengenius.DTO.LogoutRequest;
import com.kunal.gardengenius.DTO.RefreshTokenRequest;
import com.kunal.gardengenius.DTO.RefreshTokenResponse;
import com.kunal.gardengenius.DTO.UserDTO;
import com.kunal.gardengenius.entity.AuthToken;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;
import com.kunal.gardengenius.service.AuthTokenService;
import com.kunal.gardengenius.service.JwtUtils;
import com.kunal.gardengenius.service.RefreshTokenService;
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
	public ResponseEntity<RefreshTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
		String refreshToken = request.getRefreshToken();

		if (refreshToken == null) {
			return ResponseEntity.badRequest().body(new RefreshTokenResponse(null, "Refresh token is required"));
		}

		if (!refreshTokenService.validateRefreshToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new RefreshTokenResponse(null, "Invalid or expired refresh token"));
		}

		try {
			String username = refreshTokenService.getUserNameFromRefreshToken(refreshToken);
			String newAccessToken = refreshTokenService.createNewAccessToken(username);

			return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, "Access token refreshed successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RefreshTokenResponse(null, "Error occurred while refreshing token"));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@ModelAttribute User user,
			@RequestParam(required = false) MultipartFile profileImage) {
		try {
			Optional<User> existingUser = repository.findByEmail(user.getEmail());
			if (existingUser.isPresent()) {
				throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
			}

			// Handle profile image upload
			if (profileImage != null && !profileImage.isEmpty()) {
				String imageUrl = service.uploadProfileImage(profileImage);
				user.setProfileImageUrl(imageUrl);
			}

			// Proceed with the registration logic
			service.addUser(user);
			return ResponseEntity.ok("User registered successfully!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading profile image: " + e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
		String refreshToken = logoutRequest.getRefreshToken();

		// Validate the refresh token
		if (refreshToken == null || refreshToken.isEmpty()) {
			return ResponseEntity.badRequest().body("Refresh token is missing");
		}

		boolean isRefreshTokenRemoved = refreshTokenService.removeRefreshToken(refreshToken);

		if (!isRefreshTokenRemoved) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
		}

		// Clear the security context
		SecurityContextHolder.clearContext();

		// Return a success response
		return ResponseEntity.ok("Logout successful");
	}

	@GetMapping("/profile")
	public ResponseEntity<Object> getUserHome() {
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
				userDTO.setProfileImageUrl(user.getProfileImageUrl()); // Assuming User entity has this field
				return ResponseEntity.ok(userDTO);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user information");
		}
	}

	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(Authentication authentication, @ModelAttribute User updatedUser,
			@RequestParam(required = false) MultipartFile profileImage) {
		try {
			String currentUsername = authentication.getName();
			User currentUser = repository.findByUsername(currentUsername);
			if (currentUser == null) {
				throw new IllegalArgumentException("User not found");
			}

			// Check if email is being changed and if it's already in use
			if (!currentUser.getEmail().equals(updatedUser.getEmail())) {
				Optional<User> existingUser = repository.findByEmail(updatedUser.getEmail());
				if (existingUser.isPresent()) {
					throw new IllegalArgumentException("Email already in use: " + updatedUser.getEmail());
				}
			}

			// Handle profile image upload
			if (profileImage != null && !profileImage.isEmpty()) {
				String imageUrl = service.uploadProfileImage(profileImage);
				updatedUser.setProfileImageUrl(imageUrl);
			} else {
				// Keep the existing profile image URL if no new image is uploaded
				updatedUser.setProfileImageUrl(currentUser.getProfileImageUrl());
			}

			// Update user details
			updatedUser.setId(currentUser.getId()); // Ensure we're updating the correct user

			updatedUser.setPassword(currentUser.getPassword()); // Retain the current password

			service.updateUser(updatedUser);

			return ResponseEntity.ok("Profile updated successfully!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error uploading profile image: " + e.getMessage());
		}
	}

	@DeleteMapping("/profile/image")
	public ResponseEntity<?> removeProfileImage(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
		String username = userDetails.getUsername();
		User user = repository.findByUsername(username);
		if (user == null) {
			throw new IllegalArgumentException("User not found");
		}
		if (user.getProfileImageUrl() != null) {
			// Extract public ID from Cloudinary URL
			String publicId = extractPublicIdFromUrl(user.getProfileImageUrl());
			service.deleteImage(publicId);

			user.setProfileImageUrl(null);
			service.updateUserProfile(username, user);
		}

		return ResponseEntity.ok().build();
	}

	private String extractPublicIdFromUrl(String imageUrl) {

		String[] parts = imageUrl.split("/");
		String fileName = parts[parts.length - 1];
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
}
