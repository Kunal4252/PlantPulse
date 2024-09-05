package com.kunal.gardengenius.entity;

import java.time.LocalDateTime; // Import statement for LocalDateTime

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "auth_token", indexes = { @Index(name = "idx_auth_token_user", columnList = "user_id"),
		@Index(name = "idx_auth_token_token", columnList = "refresh_token") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "refresh_token", nullable = false, unique = true, length = 2048)
	private String refreshToken;

	private LocalDateTime expiryDate;
}
