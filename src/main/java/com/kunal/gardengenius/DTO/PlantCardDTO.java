package com.kunal.gardengenius.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantCardDTO {
	private Integer id;
	private String name;
	private String family;
	private String genus;
	private String alternativeNames;
}