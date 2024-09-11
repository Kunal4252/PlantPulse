package com.kunal.gardengenius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kunal.gardengenius.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByTitleContainingIgnoreCase(String title);

	@Query("SELECT p FROM Post p ORDER BY p.createdDate DESC")
	List<Post> findAllOrderedByDateDesc();

	@Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdDate DESC")
	List<Post> findByUserIdOrderedByDateDesc(@Param("userId") Long userId);
}
