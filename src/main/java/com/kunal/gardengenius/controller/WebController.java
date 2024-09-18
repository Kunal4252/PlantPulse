package com.kunal.gardengenius.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

	@GetMapping("/plantsearch")
	public String plantSearch() {
		return "plantsearch";
	}

	@GetMapping("/editprofile")
	public String editProfile() {
		return "editprofile";
	}

	@GetMapping("api/placeholder/{width}/{height}")
	public ResponseEntity<String> getPlaceholderSvg(@PathVariable int width, @PathVariable int height)
			throws IOException {

		// Load the original SVG from the static folder
		Path path = Paths.get("src/main/resources/static/placeholder.svg");
		String svg = new String(Files.readAllBytes(path));

		// Modify SVG dimensions
		String modifiedSvg = svg.replaceFirst("(?<=<svg[^>]+width=\")[^\"]*(?=\")", String.valueOf(width))
				.replaceFirst("(?<=<svg[^>]+height=\")[^\"]*(?=\")", String.valueOf(height));

		// Set the content type to SVG
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("image/svg+xml"));

		return ResponseEntity.ok().headers(headers).body(modifiedSvg);
	}
}
