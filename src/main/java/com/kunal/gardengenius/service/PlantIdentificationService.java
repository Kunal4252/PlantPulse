package com.kunal.gardengenius.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.gardengenius.DTO.PlantIdentificationResult;

@Service
public class PlantIdentificationService {
	private final RestTemplate restTemplate;
	private final String apiKey;
	private final ObjectMapper objectMapper;
	private static final String PLANT_NET_API_URL = "https://my-api.plantnet.org/v2/identify/all";

	public PlantIdentificationService(RestTemplate restTemplate, ObjectMapper objectMapper,
			@Value("${plantnet.api.key}") String apiKey) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.apiKey = apiKey;
	}

	public List<PlantIdentificationResult> identifyPlant(List<MultipartFile> images, List<String> organs)
			throws PlantIdentificationException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		for (int i = 0; i < images.size(); i++) {
			body.add("images", images.get(i).getResource());
			body.add("organs", organs.get(i));
		}

		String url = PLANT_NET_API_URL + "?api-key=" + apiKey;
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
			return processApiResponse(response.getBody());
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				throw new PlantIdentificationException(
						"Invalid image or request. Please ensure you're uploading a clear image of a plant.");
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				throw new PlantIdentificationException("API authentication failed. Please check your API key.");
			} else {
				throw new PlantIdentificationException(
						"An error occurred while identifying the plant. Please try again later.");
			}
		} catch (Exception e) {
			throw new PlantIdentificationException("An unexpected error occurred. Please try again later.");
		}
	}

	private List<PlantIdentificationResult> processApiResponse(String apiResponseJson)
			throws PlantIdentificationException {
		List<PlantIdentificationResult> results = new ArrayList<>();
		try {
			JsonNode root = objectMapper.readTree(apiResponseJson);
			JsonNode resultsNode = root.get("results");

			if (resultsNode == null || resultsNode.isEmpty()) {
				throw new PlantIdentificationException(
						"No plants were identified from the provided image. Please try with a clearer image.");
			}

			int count = Math.min(resultsNode.size(), 2); // Get top 2 results
			for (int i = 0; i < count; i++) {
				JsonNode result = resultsNode.get(i);
				PlantIdentificationResult plantResult = new PlantIdentificationResult();
				plantResult.setScientificName(result.at("/species/scientificName").asText());
				JsonNode commonNamesNode = result.at("/species/commonNames");
				String commonNames = StreamSupport.stream(commonNamesNode.spliterator(), false).map(JsonNode::asText)
						.collect(Collectors.joining(", "));
				plantResult.setCommonNames(commonNames);
				plantResult.setFamily(result.at("/species/family/scientificName").asText());
				plantResult.setGenus(result.at("/species/genus/scientificName").asText());
				plantResult.setGbifId(result.at("/gbif/id").asText());
				plantResult.setPowoId(result.at("/powo/id").asText());
				results.add(plantResult);
			}
		} catch (Exception e) {
			throw new PlantIdentificationException("Error processing the identification results. Please try again.");
		}
		return results;
	}
}