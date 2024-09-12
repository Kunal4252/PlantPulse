package com.kunal.gardengenius.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
	private int license;

	@JsonProperty("license_name")
	private String licenseName;

	@JsonProperty("license_url")
	private String licenseUrl;

	@JsonProperty("original_url")
	private String originalUrl;

	@JsonProperty("regular_url")
	private String regularUrl;

	@JsonProperty("medium_url")
	private String mediumUrl;

	@JsonProperty("small_url")
	private String smallUrl;

	private String thumbnail;
	// Getters and setters
	// Constructor
}