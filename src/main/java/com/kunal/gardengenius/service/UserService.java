package com.kunal.gardengenius.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Cloudinary cloudinary;

	public void addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);
	}

	public void updateUser(User user) {
		repository.save(user);
	}

	public String uploadProfileImage(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Failed to store empty file.");
		}

		Map<String, String> params = new HashMap<>();
		params.put("folder", "profile_images");
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

		return (String) uploadResult.get("secure_url");
	}

	public void deleteImage(String publicId) throws IOException {
		cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
	}

	public User getUserByUsername(String username) {
		User user = repository.findByUsername(username);
		if (user != null) {
			return user; // Return the User object if found
		} else {
			throw new UsernameNotFoundException("User with username: " + username + " not found"); // Handle if no user
																									// is found
		}
	}

	@Transactional
	public User updateUserProfile(String username, User updatedUser) {
		User user = repository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User with username: " + username + " not found");
		}
		// Update user fields
		user.setFirstName(updatedUser.getFirstName());
		user.setLastName(updatedUser.getLastName());
		user.setEmail(updatedUser.getEmail());
		user.setPhoneNumber(updatedUser.getPhoneNumber());
		user.setAddress(updatedUser.getAddress());

		// Update profile image URL if provided
		if (updatedUser.getProfileImageUrl() != null) {
			user.setProfileImageUrl(updatedUser.getProfileImageUrl());
		}

		// Save and return the updated user
		updatedUser.setPassword(user.getPassword());
		return repository.save(user);
	}
}
