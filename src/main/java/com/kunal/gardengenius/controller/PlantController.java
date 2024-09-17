package com.kunal.gardengenius.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.PlantDTO;
import com.kunal.gardengenius.service.PlantService;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

	@Autowired
	private PlantService plantService;

	@GetMapping("/search")
	public ResponseEntity<List<PlantDTO>> searchPlants(@RequestParam String query) {
		List<PlantDTO> plants = plantService.searchPlants(query);
		return ResponseEntity.ok(plants);
	}
}