package com.kunal.gardengenius.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPageResponseDTO {
	private List<PostCreationResponseDTO> posts;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int pageNumber;
}
