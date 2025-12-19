package com.hoxuanthai.be.lastdance.ratelimit;

import io.github.bucket4j.Bandwidth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum RateLimitType {

    LOGIN(5, Duration.ofMinutes(1)),
    UPLOAD_HEALTH_DATA(10, Duration.ofMinutes(1));

    private final int capacity; // Số lượt request tối đa
    private final Duration duration; // Thời gian làm mới

    // Phương thức để tạo Bandwidth từ thông tin của enum
    public Bandwidth getBandwidth() {
        return Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, duration)
                .build();
    }
}
