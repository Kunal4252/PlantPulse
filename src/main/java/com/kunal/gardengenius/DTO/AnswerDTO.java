package com.kunal.gardengenius.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

	private Long id;
	private String content;
	private LocalDateTime createdDate;
	private Long userId;
	private String userName;
	private String profileImageUrl;

}
