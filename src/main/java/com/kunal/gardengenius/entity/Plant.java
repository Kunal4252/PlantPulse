package com.kunal.gardengenius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "plants", indexes = { @Index(name = "idx_plant_name", columnList = "name"),
		@Index(name = "idx_plant_alternative_names", columnList = "alternative_names") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plant {

	@Id
	private Integer id;

	private String name;

	@Column(name = "scientific_name")
	private String scientificName;

	@Column(name = "alternative_names")
	private String alternativeNames;

	private String family;

	private String genus;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String sunlight;

	@Column(columnDefinition = "TEXT")
	private String soil;

	@Column(columnDefinition = "TEXT")
	private String temperature;

	@Column(name = "watering_conditions", columnDefinition = "TEXT")
	private String wateringConditions;

	@Column(name = "sowing_depth", columnDefinition = "TEXT")
	private String sowingDepth;

	@Column(name = "spacing_between_plants", columnDefinition = "TEXT")
	private String spacingBetweenPlants;

	@Column(name = "spacing_between_rows", columnDefinition = "TEXT")
	private String spacingBetweenRows;

	@Column(name = "watering_instructions", columnDefinition = "TEXT")
	private String wateringInstructions;

	@Column(name = "fertilizing_instructions", columnDefinition = "TEXT")
	private String fertilizingInstructions;

	@Column(name = "pruning_instructions", columnDefinition = "TEXT")
	private String pruningInstructions;

	@Column(name = "days_to_maturity", columnDefinition = "TEXT")
	private String daysToMaturity;

	@Column(name = "harvest_season", columnDefinition = "TEXT")
	private String harvestSeason;

	@Column(name = "harvest_method", columnDefinition = "TEXT")
	private String harvestMethod;

	@Column(columnDefinition = "TEXT")
	private String pests;

	@Column(columnDefinition = "TEXT")
	private String diseases;

	@Column(name = "companion_plants", columnDefinition = "TEXT")
	private String companionPlants;

	@Column(columnDefinition = "TEXT")
	private String notes;
}