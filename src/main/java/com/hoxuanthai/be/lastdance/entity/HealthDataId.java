package com.hoxuanthai.be.lastdance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataId implements Serializable {
    private UUID id;
    private LocalDateTime timestamp;
}
