package com.kunal.gardengenius.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreationResponseDTO {

	private Long postId;
	private String title;
	private String content;
	private LocalDateTime createdDate;
	private Long userId;
	private String userName;
	private String profileImageUrl;
	private List<AnswerDTO> answers;
	private int likeCount;
	private boolean likedByCurrentUser;

}
