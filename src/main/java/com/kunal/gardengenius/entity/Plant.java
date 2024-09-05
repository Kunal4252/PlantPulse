package com.kunal.gardengenius.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "plants", indexes = { @Index(name = "idx_plant_name", columnList = "name"),
		@Index(name = "idx_plant_species", columnList = "species"),
		@Index(name = "idx_plant_soil_type", columnList = "soilType"),
		@Index(name = "idx_plant_sunlight_requirement", columnList = "sunlightRequirement") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String species;

	@Column(nullable = false)
	private LocalDateTime plantedDate;

	@Column(nullable = false)
	private String soilType;

	@Column(nullable = false)
	private String sunlightRequirement;

	@ManyToOne
	@JoinColumn(name = "garden_id", nullable = false)
	private Garden garden;
}
