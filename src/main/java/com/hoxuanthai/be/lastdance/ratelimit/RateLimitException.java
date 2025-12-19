package com.hoxuanthai.be.lastdance.ratelimit;

public class RateLimitException extends RuntimeException {

    private final RateLimitType rateLimitType;
    private final long retryAfterSeconds;

    public RateLimitException(RateLimitType rateLimitType) {
        super("Rate limit exceeded for: " + rateLimitType.name());
        this.rateLimitType = rateLimitType;
        this.retryAfterSeconds = rateLimitType.getDuration().getSeconds();
    }

    public RateLimitType getRateLimitType() {
        return rateLimitType;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

