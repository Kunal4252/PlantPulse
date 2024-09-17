package com.kunal.gardengenius.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.DTO.PlantDTO;
import com.kunal.gardengenius.entity.Plant;
import com.kunal.gardengenius.repository.PlantRepository;

@Service
public class PlantService {

	@Autowired
	private PlantRepository plantRepository;

	public List<PlantDTO> searchPlants(String query) {
		List<Plant> plants = plantRepository.findByNameContainingIgnoreCase(query);
		return plants.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private PlantDTO convertToDTO(Plant plant) {
		PlantDTO dto = new PlantDTO();
		BeanUtils.copyProperties(plant, dto);
		return dto;
	}
}