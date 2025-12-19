package com.hoxuanthai.be.lastdance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DEVICES")
@EqualsAndHashCode(callSuper = true)
public class Device extends BaseEntity {

    @Column(name="device_uuid", unique = true, nullable = false)
    private String deviceUuid;

    @Column(name="device_name", nullable = false)
    private String deviceName;

    @Column(name="is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
