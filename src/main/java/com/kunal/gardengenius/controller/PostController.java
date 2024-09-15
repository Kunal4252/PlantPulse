package com.kunal.gardengenius.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.service.PostService;
import com.kunal.gardengenius.service.UserService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	// Get all posts
	@GetMapping
	public ResponseEntity<List<PostCreationResponseDTO>> getAllPosts(@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			List<Post> posts = postService.getAllPosts();
			List<PostCreationResponseDTO> response = posts.stream().map(post -> convertToDTO(post, currentUser.getId()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Get posts by user ID
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostCreationResponseDTO>> getPostsByUserId(@PathVariable Long userId) {
		try {
			List<Post> posts = postService.getPostsByUserId(userId);
			List<PostCreationResponseDTO> response = posts.stream().map(post -> convertToDTO(post, userId))
					.collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Delete a post by ID
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
			List<Post> posts = postService.searchPostsByTitle(title);
			List<PostCreationResponseDTO> postDTOs = posts.stream().map(post -> convertToDTO(post, null)) // No current
																											// user for
																											// searching
					.collect(Collectors.toList());
			return ResponseEntity.ok(postDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Create a post
	@PostMapping
	public ResponseEntity<PostCreationResponseDTO> createPost(@RequestBody Post post,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			post.setUser(currentUser); // Set the user as the creator of the post
			Post createdPost = postService.createPost(post);
			PostCreationResponseDTO response = convertToDTO(createdPost, currentUser.getId());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Like a post
	@PostMapping("/{postId}/like")
	public ResponseEntity<Void> likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			postService.addLike(postId, currentUser.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Unlike a post
	@DeleteMapping("/{postId}/like")
	public ResponseEntity<Void> unlikePost(@PathVariable Long postId,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			postService.removeLike(postId, currentUser.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Helper method to convert a Post to PostCreationResponseDTO
	private PostCreationResponseDTO convertToDTO(Post post, Long currentUserId) {
		List<AnswerDTO> sortedAnswers = post.getAnswers() != null ? post.getAnswers().stream()
				.sorted((a1, a2) -> Integer.compare(a2.getLikes().size(), a1.getLikes().size()))
				.map(answer -> new AnswerDTO(answer.getId(), answer.getContent(), answer.getCreatedDate(),
						answer.getUser() != null ? answer.getUser().getId() : null,
						answer.getUser() != null ? answer.getUser().getUsername() : null,
						answer.getUser() != null ? answer.getUser().getProfileImageUrl() : null,
						answer.getLikes().size(),
						currentUserId != null
								&& answer.getLikes().stream().anyMatch(user -> user.getId().equals(currentUserId))))
				.collect(Collectors.toList()) : Collections.emptyList();

		return new PostCreationResponseDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedDate(),
				post.getUser() != null ? post.getUser().getId() : null,
				post.getUser() != null ? post.getUser().getUsername() : null,
				post.getUser() != null ? post.getUser().getProfileImageUrl() : null, sortedAnswers,
				post.getLikedBy().size(), currentUserId != null
						&& post.getLikedBy().stream().anyMatch(user -> user.getId().equals(currentUserId)));
	}
}
