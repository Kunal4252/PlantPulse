package com.kunal.gardengenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;

@Service
public class UserInfoDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub

		User user = repository.findByUsername(username);
		if (user.equals(null)) {
			throw new UsernameNotFoundException("User does not exists");
		}

		return new UserInfoDetails(user);

	}

}
