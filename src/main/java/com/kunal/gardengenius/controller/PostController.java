package com.kunal.gardengenius.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
			List<PostCreationResponseDTO> response = posts.stream().map(this::convertToDTO) // Use convertToDTO method
					.collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostCreationResponseDTO>> getPostsByUserId(@PathVariable Long userId) {
		try {
			List<Post> posts = postService.getPostsByUserId(userId);
			List<PostCreationResponseDTO> response = posts.stream().map(this::convertToDTO) // Use convertToDTO method
					.collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable Long id) {
		try {
			boolean isDeleted = postService.deletePost(id);
			if (isDeleted) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Search posts by title
	@GetMapping("/search")
	public ResponseEntity<List<PostCreationResponseDTO>> searchPosts(@RequestParam String title) {
		try {
			// Fetch all posts matching the search title without pagination
			List<Post> posts = postService.searchPostsByTitle(title);

			// Convert posts to DTOs using convertToDTO method
			List<PostCreationResponseDTO> postDTOs = posts.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			return ResponseEntity.ok(postDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Create a post
	@PostMapping
	public ResponseEntity<PostCreationResponseDTO> createPost(@RequestBody Post post) {
		try {
			Post createdPost = postService.createPost(post);
			PostCreationResponseDTO response = convertToDTO(createdPost); // Use convertToDTO method
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	private PostCreationResponseDTO convertToDTO(Post post) {
		return new PostCreationResponseDTO(post.getId(), // postId
				post.getTitle(), // title
				post.getContent(), // content
				post.getCreatedDate(), // createdDate
				post.getUser() != null ? post.getUser().getId() : null, // userId
				post.getUser() != null ? post.getUser().getUsername() : null, // userName
				post.getUser() != null ? post.getUser().getProfileImageUrl() : null, // profileImageUrl
				post.getAnswers() != null ? post.getAnswers().stream() // answers (map Answer to AnswerDTO)
						.map(answer -> new AnswerDTO(answer.getId(), answer.getContent(), answer.getCreatedDate(),
								answer.getUser() != null ? answer.getUser().getId() : null, // userId of answer
								answer.getUser() != null ? answer.getUser().getUsername() : null, // userName of answer
								answer.getUser() != null ? answer.getUser().getProfileImageUrl() : null // profileImageUrl
																										// of answer
						)).collect(Collectors.toList()) : Collections.emptyList() // Handle null or empty answers
		);
	}
}
