package com.kunal.gardengenius.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.AnswerDTO;
import com.kunal.gardengenius.entity.Answer;
import com.kunal.gardengenius.entity.User;
import com.kunal.gardengenius.service.AnswerService;
import com.kunal.gardengenius.service.UserService;

@RestController
@RequestMapping("/api/posts/{postId}/answers")
public class AnswerController {
	@Autowired
	private AnswerService answerService;

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<List<AnswerDTO>> getAllAnswersForPost(@PathVariable Long postId,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			List<Answer> answers = answerService.getAllAnswersForPost(postId);
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			List<AnswerDTO> answerDTOs = answers.stream().map(answer -> convertToDTO(answer, currentUser))
					.collect(Collectors.toList());
			return ResponseEntity.ok(answerDTOs);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<AnswerDTO> createAnswer(@PathVariable Long postId, @RequestBody Answer answer,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			Answer createdAnswer = answerService.createAnswer(postId, answer);
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			AnswerDTO createdAnswerDTO = convertToDTO(createdAnswer, currentUser);
			return ResponseEntity.ok(createdAnswerDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/{answerId}/like")
	public ResponseEntity<Void> likeAnswer(@PathVariable Long postId, @PathVariable Long answerId,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			answerService.addLike(answerId, currentUser.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{answerId}/like")
	public ResponseEntity<Void> unlikeAnswer(@PathVariable Long postId, @PathVariable Long answerId,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			User currentUser = userService.getUserByUsername(userDetails.getUsername());
			answerService.removeLike(answerId, currentUser.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private AnswerDTO convertToDTO(Answer answer, User currentUser) {
		return new AnswerDTO(answer.getId(), answer.getContent(), answer.getCreatedDate(),
				answer.getUser() != null ? answer.getUser().getId() : null,
				answer.getUser() != null ? answer.getUser().getUsername() : null,
				answer.getUser() != null ? answer.getUser().getProfileImageUrl() : null, answer.getLikes().size(),
				answer.getLikes().contains(currentUser));
	}
}