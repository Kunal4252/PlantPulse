package com.kunal.gardengenius.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.AnswerDTO;
import com.kunal.gardengenius.entity.Answer;
import com.kunal.gardengenius.service.AnswerService;

@RestController
@RequestMapping("/api/posts/{postId}/answers")
public class AnswerController {

	@Autowired
	private AnswerService answerService;

	// Get all answers for a specific post
	@GetMapping
	public ResponseEntity<List<AnswerDTO>> getAllAnswersForPost(@PathVariable Long postId) {
		try {
			List<Answer> answers = answerService.getAllAnswersForPost(postId);

			// Directly convert the list of Answer entities to AnswerDTOs using streams
			List<AnswerDTO> answerDTOs = answers.stream()
					.map(answer -> new AnswerDTO(answer.getId(), answer.getContent(), answer.getCreatedDate(),
							answer.getUser() != null ? answer.getUser().getId() : null,
							answer.getUser() != null ? answer.getUser().getUsername() : null,
							answer.getUser() != null ? answer.getUser().getProfileImageUrl() : null))
					.collect(Collectors.toList());

			return ResponseEntity.ok(answerDTOs);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<AnswerDTO> createAnswer(@PathVariable Long postId, @RequestBody Answer answer) {
		try {
			Answer createdAnswer = answerService.createAnswer(postId, answer);

			// Directly convert the created Answer entity to an AnswerDTO
			AnswerDTO createdAnswerDTO = new AnswerDTO(createdAnswer.getId(), createdAnswer.getContent(),
					createdAnswer.getCreatedDate(),
					createdAnswer.getUser() != null ? createdAnswer.getUser().getId() : null,
					createdAnswer.getUser() != null ? createdAnswer.getUser().getUsername() : null,
					createdAnswer.getUser() != null ? createdAnswer.getUser().getProfileImageUrl() : null);

			return ResponseEntity.ok(createdAnswerDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

}
