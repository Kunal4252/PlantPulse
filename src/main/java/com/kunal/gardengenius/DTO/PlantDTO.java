package com.kunal.gardengenius.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantDTO {
	private Long id;

	@JsonProperty("common_name")
	private String commonName;

	@JsonProperty("scientific_name")
	private List<String> scientificName;

	@JsonProperty("other_name")
	private List<String> otherName;

	private String cycle;
	private String watering;
	private List<String> sunlight;

	@JsonProperty("default_image")
	private ImageDTO defaultImage;

	// Getters and setters
	// Constructor
}