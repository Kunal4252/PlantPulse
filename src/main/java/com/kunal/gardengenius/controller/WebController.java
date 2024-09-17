package com.kunal.gardengenius.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

	@GetMapping("/")
	public String home() {
		return "home"; // This will resolve to src/main/resources/templates/home.html
	}

	@GetMapping("/signUp")
	public String register() {
		return "signUp"; // This will resolve to src/main/resources/templates/home.html
	}

	@GetMapping("/signIn")
	public String login() {
		return "signIn"; // This will resolve to src/main/resources/templates/home.html
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
	
	@GetMapping("/plantidentification")
	public String plantIdentification() {
		return "plantidentification";
	}

}
