package com.kunal.gardengenius.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantIdentificationResult {
	private String scientificName;
	private String commonNames;
	private String family;
	private String genus;
	private String gbifId;
	private String powoId;

	// Getters and setters
}