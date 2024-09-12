package com.kunal.gardengenius.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kunal.gardengenius.DTO.PlantDTO;
import com.kunal.gardengenius.service.PlantService;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

	private final PlantService plantService;

	public PlantController(PlantService plantService) {
		this.plantService = plantService;
	}

	@GetMapping("/search")
	public List<PlantDTO> searchPlants(@RequestParam String query) {
		return plantService.searchPlants(query);
	}
}
