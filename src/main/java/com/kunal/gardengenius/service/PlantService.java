package com.kunal.gardengenius.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kunal.gardengenius.DTO.PlantCardDTO;
import com.kunal.gardengenius.DTO.PlantDetailDTO;
import com.kunal.gardengenius.entity.Plant;
import com.kunal.gardengenius.repository.PlantRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class PlantService {

	@Autowired
	private PlantRepository plantRepository;

	public List<PlantCardDTO> searchPlants(String query) {
		Specification<Plant> spec = (root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (query != null && !query.isEmpty()) {
				String likePattern = "%" + query.toLowerCase() + "%";
				predicates.add(criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("alternativeNames")), likePattern)));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		List<Plant> plants = plantRepository.findAll(spec);
		return plants.stream().map(this::convertToCardDTO).collect(Collectors.toList());
	}

	public PlantDetailDTO getPlantDetails(String name) {
		Plant plant = plantRepository.findByNameIgnoreCase(name)
				.orElseThrow(() -> new RuntimeException("Plant not found"));
		return convertToDetailDTO(plant);
	}

	private PlantCardDTO convertToCardDTO(Plant plant) {
		PlantCardDTO dto = new PlantCardDTO();
		BeanUtils.copyProperties(plant, dto);
		return dto;
	}

	private PlantDetailDTO convertToDetailDTO(Plant plant) {
		PlantDetailDTO dto = new PlantDetailDTO();
		BeanUtils.copyProperties(plant, dto);
		return dto;
	}
}