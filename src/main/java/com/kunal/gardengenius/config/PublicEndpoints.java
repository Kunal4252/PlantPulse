package com.kunal.gardengenius.config;

import org.springframework.stereotype.Component;

@Component
public class PublicEndpoints {

	public static final String[] PUBLIC_URLS = { "/api/users/register", "/api/users/login", "/api/users/refresh",
			"/api/users/logout", "/", "/signUp", "/signIn", "/userhome", "/communityPost", "/plantidentification",
			"/plantsearch", "/favicon.ico", "/api/placeholder/{width}/{height}", "/editprofile", "/post", "/js/**" };
}