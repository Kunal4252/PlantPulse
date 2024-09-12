package com.kunal.gardengenius.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kunal.gardengenius.DTO.PerenualApiResponse;
import com.kunal.gardengenius.DTO.PlantDTO;

@Service
public class PlantService {

	@Value("${perenual.api.key}")
	private String apiKey;

	private final String API_URL = "https://perenual.com/api/species-list";

	private final RestTemplate restTemplate;

	public PlantService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<PlantDTO> searchPlants(String query) {
		String url = API_URL + "?key=" + apiKey + "&q=" + query;
		PerenualApiResponse response = restTemplate.getForObject(url, PerenualApiResponse.class);
		if (response != null && response.getData() != null) {
			return response.getData().stream().filter(plant -> plant.getId() != null) // Filter out any plants with null
																						// ID
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
