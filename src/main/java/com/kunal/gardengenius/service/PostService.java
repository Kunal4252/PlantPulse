package com.kunal.gardengenius.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kunal.gardengenius.entity.Post;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.PostRepository;
import com.kunal.gardengenius.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	// Method for searching posts by title
	public List<Post> searchPostsByTitle(String title) {
		// Assuming you have a repository method for searching posts by title
		return postRepository.searchByTitleAndOrderByLikes(title);
	}

	// Get all posts
	public List<Post> getAllPosts() {
		return postRepository.findAllOrderedByDateDesc();
	}

	public List<Post> getPostsByUserId(Long userId) {
		return postRepository.findByUserIdOrderedByDateDesc(userId);
	}

	public boolean deletePost(Long id) {
		if (postRepository.existsById(id)) {
			postRepository.deleteById(id);
			return true;
		}
		return false;
	}

	// Get a post by ID
	public Optional<Post> getPostById(Long id) {
		return postRepository.findById(id);
	}

	// Create a post
	public Post createPost(Post post) {
		Optional<User> user = userRepository.findById(post.getUser().getId());
		if (user.isPresent()) {
			post.setCreatedDate(LocalDateTime.now());
			post.setUser(user.get());
			return postRepository.save(post);
		} else {
			throw new IllegalArgumentException("User not found");
		}
	}

	@Transactional
	public void addLike(Long postId, Long userId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

		post.getLikedBy().add(user);
		postRepository.save(post);
	}

	@Transactional
	public void removeLike(Long postId, Long userId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

		post.getLikedBy().remove(user);
		postRepository.save(post);
	}
}
