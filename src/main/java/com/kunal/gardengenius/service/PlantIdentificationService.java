package com.kunal.gardengenius.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlantIdentificationService {

	private final RestTemplate restTemplate;
	private final String apiKey;
	private static final String PLANT_NET_API_URL = "https://my-api.plantnet.org/v2/identify/all";

	public PlantIdentificationService(RestTemplate restTemplate, @Value("${plantnet.api.key}") String apiKey) {
		this.restTemplate = restTemplate;
		this.apiKey = apiKey;
	}

	public ResponseEntity<String> identifyPlant(List<MultipartFile> images, List<String> organs) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		for (int i = 0; i < images.size(); i++) {
			body.add("images", images.get(i).getResource());
			body.add("organs", organs.get(i));
		}

		String url = PLANT_NET_API_URL + "?api-key=" + apiKey;

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		return restTemplate.postForEntity(url, requestEntity, String.class);
	}
}
