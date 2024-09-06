package com.kunal.gardengenius.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;

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

	public String uploadProfileImage(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Failed to store empty file.");
		}

		Map<String, String> params = new HashMap<>();
		params.put("folder", "profile_images");
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

		return (String) uploadResult.get("secure_url");
	}
}
