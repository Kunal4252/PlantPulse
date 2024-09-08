package com.kunal.gardengenius.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.AnswerDTO;
import com.kunal.gardengenius.DTO.PostCreationResponseDTO;
import com.kunal.gardengenius.entity.Post;
import com.kunal.gardengenius.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	private PostService postService;

	// Get all posts
	@GetMapping
	public ResponseEntity<List<PostCreationResponseDTO>> getAllPosts() {
		try {
			List<Post> posts = postService.getAllPosts();
			List<PostCreationResponseDTO> response = posts.stream()
					.map(post -> new PostCreationResponseDTO(post.getId(), post.getTitle(), post.getContent(),
							post.getCreatedDate(), post.getUser() != null ? post.getUser().getId() : null,
							post.getUser() != null ? post.getUser().getUsername() : null,
							post.getUser() != null ? post.getUser().getProfileImageUrl() : null,
							post.getAnswers() != null
									? post.getAnswers().stream()
											.map(answer -> new AnswerDTO(answer.getId(), answer.getContent(),
													answer.getCreatedDate(),
													answer.getUser() != null ? answer.getUser().getId() : null,
													answer.getUser() != null ? answer.getUser().getUsername() : null,
													answer.getUser() != null ? answer.getUser().getProfileImageUrl()
															: null))
											.collect(Collectors.toList())
									: Collections.emptyList()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Get a post by ID
	@GetMapping("/{id}")
	public ResponseEntity<Post> getPostById(@PathVariable Long id) {
		Optional<Post> post = postService.getPostById(id);
		return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Create a post
	@PostMapping
	public ResponseEntity<PostCreationResponseDTO> createPost(@RequestBody Post post) {
		try {
			Post createdPost = postService.createPost(post);
			PostCreationResponseDTO response = new PostCreationResponseDTO(createdPost.getId(), createdPost.getTitle(),
					createdPost.getContent(), createdPost.getCreatedDate(),
					createdPost.getUser() != null ? createdPost.getUser().getId() : null,
					createdPost.getUser() != null ? createdPost.getUser().getUsername() : null,
					createdPost.getUser() != null ? createdPost.getUser().getProfileImageUrl() : null,
					createdPost.getAnswers() != null ? createdPost.getAnswers().stream()
							.map(answer -> new AnswerDTO(answer.getId(), answer.getContent(), answer.getCreatedDate(),
									answer.getUser() != null ? answer.getUser().getId() : null,
									answer.getUser() != null ? answer.getUser().getUsername() : null,
									answer.getUser() != null ? answer.getUser().getProfileImageUrl() : null))
							.collect(Collectors.toList()) : Collections.emptyList());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}
