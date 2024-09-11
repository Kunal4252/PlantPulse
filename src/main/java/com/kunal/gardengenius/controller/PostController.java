package com.kunal.gardengenius.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostCreationResponseDTO>> getPostsByUserId(@PathVariable Long userId) {
		try {
			List<Post> posts = postService.getPostsByUserId(userId);
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

//	@GetMapping
//	public ResponseEntity<PostPageResponseDTO> getPaginatedPosts(@RequestParam(defaultValue = "0") int page,
//			@RequestParam(defaultValue = "10") int size) {
//		try {
//			Pageable pageable = PageRequest.of(page, size);
//			Page<Post> postPage = postService.getPaginatedPosts(pageable);
//
//			List<PostCreationResponseDTO> postDTOs = postPage.getContent().stream().map(this::convertToDTO)
//					.collect(Collectors.toList());
//
//			PostPageResponseDTO response = new PostPageResponseDTO(postDTOs, postPage.getTotalPages(),
//					postPage.getTotalElements(), postPage.getSize(), postPage.getNumber());
//
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//		}
//	}

	// Search posts by title
	@GetMapping("/search")
	public ResponseEntity<List<PostCreationResponseDTO>> searchPosts(@RequestParam String title) {
		try {
			// Fetch all posts matching the search title without pagination
			List<Post> posts = postService.searchPostsByTitle(title);

			// Convert posts to DTOs
			List<PostCreationResponseDTO> postDTOs = posts.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			return ResponseEntity.ok(postDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Get a post by IDa
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
