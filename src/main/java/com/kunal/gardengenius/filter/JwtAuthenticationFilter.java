package com.kunal.gardengenius.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.UserRepository;
import com.kunal.gardengenius.service.JwtUtils;
import com.kunal.gardengenius.service.TokenBlacklistService;
import com.kunal.gardengenius.service.UserInfoDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	@Autowired
	private UserRepository repository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateAccessToken(jwt)) {
				if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
					response.getWriter().write("Token is invalid or blacklisted");
					return;
				}
				String username = jwtUtils.getUserNameFromAccessToken(jwt);
				User user = repository.findByUsername(username);
				if (user != null) {
					UserInfoDetails userInfoDetails = new UserInfoDetails(user);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userInfoDetails, null, Collections.emptyList());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
			return; // Stop the filter chain
		} catch (JwtException e) {
			handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
			return; // Stop the filter chain
		} catch (AuthenticationException e) {
			handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
			return; // Stop the filter chain
		} catch (Exception e) {
			handleException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
			return; // Stop the filter chain
		}
	}

	private void handleException(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
	}

	public String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}
}
