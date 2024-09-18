package com.kunal.gardengenius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kunal.gardengenius.entity.Plant;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Integer> {
	List<Plant> findByNameContainingIgnoreCase(String name);

	List<Plant> findAll(Specification<Plant> spec);

	Optional<Plant> findByNameIgnoreCase(String name);

}