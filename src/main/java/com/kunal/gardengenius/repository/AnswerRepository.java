package com.kunal.gardengenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kunal.gardengenius.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
