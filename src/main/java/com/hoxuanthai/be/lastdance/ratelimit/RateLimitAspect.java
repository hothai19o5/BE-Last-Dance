package com.hoxuanthai.be.lastdance.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect xử lý giới hạn tần suất (rate limiting) cho các phương thức được chú thích bằng @RateLimit.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String keyValue = resolveKeyValue(rateLimit.keyBy());
        String key = rateLimitService.createKey(rateLimit.type(), rateLimit.keyBy(), keyValue);

        if (!rateLimitService.tryConsume(key, rateLimit.type())) {
            log.warn("Rate limit exceeded - Type: {}, Key: {}, KeyValue: {}",
                    rateLimit.type(), rateLimit.keyBy(), keyValue);
            throw new RateLimitException(rateLimit.type());
        }

        log.debug("Rate limit passed - Type: {}, Key: {}, Remaining: {}",
                rateLimit.type(), key, rateLimitService.getAvailableTokens(key, rateLimit.type()));

        return joinPoint.proceed();
    }

    private String resolveKeyValue(KeyType keyType) {
        return switch (keyType) {
            case IP -> getClientIp();
            case USER_ID -> getUserId();
            case TOKEN -> getToken();
            case ROLE -> getUserRole();
        };
    }

    private String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return getClientIp(); // Fallback to IP if not authenticated
    }

    private String getToken() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return getClientIp();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Dùng hash của token thay vì token đầy đủ
            return String.valueOf(authHeader.hashCode());
        }
        return getClientIp(); // Fallback to IP
    }

    private String getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }
        return "ANONYMOUS";
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}