package com.hoxuanthai.be.lastdance.ratelimit;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Tạo key duy nhất cho mỗi bucket
     */
    public String createKey(RateLimitType type, KeyType keyType, String keyValue) {
        return type.name() + ":" + keyType.name() + ":" + keyValue;
    }

    /**
     * Lấy hoặc tạo bucket cho key
     */
    public Bucket resolveBucket(String key, RateLimitType type) {
        return buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(type.getBandwidth())
                .build());
    }

    /**
     * Kiểm tra và consume 1 token
     * @return true nếu được phép, false nếu bị rate limit
     */
    public boolean tryConsume(String key, RateLimitType type) {
        Bucket bucket = resolveBucket(key, type);
        return bucket.tryConsume(1);
    }

    /**
     * Lấy số token còn lại
     */
    public long getAvailableTokens(String key, RateLimitType type) {
        Bucket bucket = resolveBucket(key, type);
        return bucket.getAvailableTokens();
    }
}
