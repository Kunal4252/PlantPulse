package com.kunal.gardengenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);
	}
}
