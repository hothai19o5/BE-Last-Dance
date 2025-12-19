package com.hoxuanthai.be.lastdance.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String profilePictureUrl;

    private LocalDate dob;

    private String gender;

    private Double weightKg;

    private Double heightM;

    private Double bmi;

    private boolean enabled;
    
    private List<Long> deviceIds;

    private List<DeviceDto> devices;
}
