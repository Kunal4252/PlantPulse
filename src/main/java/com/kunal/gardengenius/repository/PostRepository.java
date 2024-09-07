package com.kunal.gardengenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kunal.gardengenius.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
