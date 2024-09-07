package com.kunal.gardengenius.controller;

import com.kunal.gardengenius.entity.Answer;
import com.kunal.gardengenius.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/answers")
public class AnswerController {

	@Autowired
	private AnswerService answerService;

	// Get all answers for a specific post
	@GetMapping
	public ResponseEntity<List<Answer>> getAllAnswersForPost(@PathVariable Long postId) {
		try {
			List<Answer> answers = answerService.getAllAnswersForPost(postId);
			return ResponseEntity.ok(answers);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Create an answer for a post
	@PostMapping
	public ResponseEntity<Answer> createAnswer(@PathVariable Long postId, @RequestBody Answer answer) {
		try {
			Answer createdAnswer = answerService.createAnswer(postId, answer);
			return ResponseEntity.ok(createdAnswer);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}
