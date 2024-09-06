package com.kunal.gardengenius.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String phoneNumber;
	private String address;
	private String profileImageUrl;
}