package com.kunal.gardengenius.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kunal.gardengenius.DTO.PlantIdentificationResult;
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
			@RequestParam("images") List<MultipartFile> images, @RequestParam("organs") List<String> organs) {
		List<PlantIdentificationResult> results = plantIdentificationService.identifyPlant(images, organs);
		if (results.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(results);
	}
}