package com.kunal.gardengenius.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/plant")
public class PlantController {

	@GetMapping("/addplant")
	public String addPlant() {
		return "plant added";

	}
}
