package com.kunal.gardengenius.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

	@GetMapping("/")
	public String home() {
		return "home"; // This will resolve to src/main/resources/templates/home.html
	}

	@GetMapping("/register")
	public String register() {
		return "register"; // This will resolve to src/main/resources/templates/home.html
	}

	@GetMapping("/login")
	public String login() {
		return "login"; // This will resolve to src/main/resources/templates/home.html
	}

	@GetMapping("/userhome")
	public String userHomePage() {
		return "userhome";
	}

	@GetMapping("/communityPost")
	public String communityPage() {
		return "communityPost";
	}
	
	@GetMapping("/post")
	public String postPage() {
		return "post";
	}

}
