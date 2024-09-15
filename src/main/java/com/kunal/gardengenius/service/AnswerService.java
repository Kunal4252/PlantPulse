package com.kunal.gardengenius.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.entity.Answer;
import com.kunal.gardengenius.entity.Post;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.repository.AnswerRepository;
import com.kunal.gardengenius.repository.PostRepository;
import com.kunal.gardengenius.repository.UserRepository;

@Service
public class AnswerService {

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	// Get all answers for a specific post
	public List<Answer> getAllAnswersForPost(Long postId) {
		Optional<Post> post = postRepository.findById(postId);
		if (post.isPresent()) {
			return post.get().getAnswers();
		} else {
			throw new IllegalArgumentException("Post not found");
		}
	}

	// Create an answer for a post
	public Answer createAnswer(Long postId, Answer answer) {
		Optional<Post> post = postRepository.findById(postId);
		Optional<User> user = userRepository.findById(answer.getUser().getId());

		if (post.isPresent() && user.isPresent()) {
			answer.setCreatedDate(LocalDateTime.now());
			answer.setPost(post.get());
			answer.setUser(user.get());
			return answerRepository.save(answer);
		} else {
			throw new IllegalArgumentException("Post or User not found");
		}
	}

	public void addLike(Long answerId, Long userId) {
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(() -> new IllegalArgumentException("Answer not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (answer.getLikes().contains(user)) {
			throw new IllegalStateException("User has already liked this answer");
		}

		answer.getLikes().add(user);
		answerRepository.save(answer);
	}

	public void removeLike(Long answerId, Long userId) {
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(() -> new IllegalArgumentException("Answer not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (!answer.getLikes().contains(user)) {
			throw new IllegalStateException("User has not liked this answer");
		}

		answer.getLikes().remove(user);
		answerRepository.save(answer);
	}
}
