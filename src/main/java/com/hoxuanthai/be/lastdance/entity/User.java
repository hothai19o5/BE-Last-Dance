package com.hoxuanthai.be.lastdance.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

	@Column(unique = true)
	private String username;

	private String password;

	private String email;

	@Column(name="first_name")
	private String firstName;

	@Column(name="last_name")
	private String lastName;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name="date_of_birth")
	private LocalDate dob;

	@Column(name="weight_kg")
	private Double weightKg;

	@Column(name="height_m")
	private Double heightM;

	@Column(name="bmi")
	private Double bmi;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Column(name="profile_picture_url")
	private String profilePictureUrl;

	@Column(name="enabled", nullable = false)
	@Builder.Default
	private Boolean enabled = true;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Device> devices;
}
