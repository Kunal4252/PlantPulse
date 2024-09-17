package com.kunal.gardengenius.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantDTO {
	private Integer id;
	private String name;
	private String scientificName;
	private String alternativeNames;
	private String family;
	private String genus;
	private String description;
	private String sunlight;
	private String soil;
	private String temperature;
	private String wateringConditions;
	private String sowingDepth;
	private String spacingBetweenPlants;
	private String spacingBetweenRows;
	private String wateringInstructions;
	private String fertilizingInstructions;
	private String pruningInstructions;
	private String daysToMaturity;
	private String harvestSeason;
	private String harvestMethod;
	private String pests;
	private String diseases;
	private String companionPlants;
	private String notes;
}
