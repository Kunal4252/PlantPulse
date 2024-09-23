package com.kunal.gardengenius.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kunal.gardengenius.DTO.PlantIdentificationResult;
import com.kunal.gardengenius.service.PlantIdentificationException;
import com.kunal.gardengenius.service.PlantIdentificationService;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "*")
public class PlantIdentificationController {
	private final PlantIdentificationService plantIdentificationService;

	public PlantIdentificationController(PlantIdentificationService plantIdentificationService) {
		this.plantIdentificationService = plantIdentificationService;
	}

	@PostMapping("/identify")
	public ResponseEntity<List<PlantIdentificationResult>> identifyPlant(
			@RequestParam("images") List<MultipartFile> images, @RequestParam("organs") List<String> organs)
			throws PlantIdentificationException {
		List<PlantIdentificationResult> results = plantIdentificationService.identifyPlant(images, organs);
		return ResponseEntity.ok(results);
	}

	@ExceptionHandler(PlantIdentificationException.class)
	public ResponseEntity<String> handlePlantIdentificationException(PlantIdentificationException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}