package com.kunal.gardengenius.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	private final Set<String> blacklistedTokens = new HashSet<>(); // For demonstration purposes

	public boolean blacklistToken(String token) {
		return blacklistedTokens.add(token);
	}

	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}
}
