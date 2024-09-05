package com.kunal.gardengenius.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

	@Value("${jwt.access.secret}")
	private String jwtAccessSecret;

	@Value("${jwt.expirationMs}")
	private int jwtExpirationMs;

	@Value("${jwt.refresh.secret}")
	private String jwtRefreshSecret;

	@Value("${jwt.refreshExpirationMs}")
	private int jwtRefreshExpirationMs;

	public String generateAccessToken(Authentication authentication) {
		return Jwts.builder().setSubject(authentication.getName()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS256, jwtAccessSecret).compact();
	}

	public String generateRefreshToken(Authentication authentication) {
		return Jwts.builder().setSubject(authentication.getName()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtRefreshExpirationMs))
				.signWith(SignatureAlgorithm.HS256, jwtRefreshSecret).compact();
	}

	public String generateAccessToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS256, jwtAccessSecret).compact();
	}

	public String generateRefreshToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtRefreshExpirationMs))
				.signWith(SignatureAlgorithm.HS256, jwtRefreshSecret).compact();
	}

	public String getUserNameFromAccessToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtAccessSecret).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public String getUserNameFromRefreshToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtAccessSecret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			// Log and handle the exception
			return false;
		}
	}

	public boolean validatRefreshToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			// Log and handle the exception
			return false;
		}
	}

}
