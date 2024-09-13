package com.kunal.gardengenius.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	public ResponseEntity<String> identifyPlant(@RequestParam("images") List<MultipartFile> images,
			@RequestParam("organs") List<String> organs) {
		return plantIdentificationService.identifyPlant(images, organs);
	}
	

}