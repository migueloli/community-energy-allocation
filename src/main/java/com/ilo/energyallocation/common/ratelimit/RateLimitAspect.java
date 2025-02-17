package com.ilo.energyallocation.common.ratelimit;

import com.ilo.energyallocation.common.property.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final ProxyManager<String> proxyManager;
    private final RateLimitProperties rateLimitProperties;

    private String getClientIp(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    @CircuitBreaker(name = "rateLimit", fallbackMethod = "rateLimitFallback")
    @Retry(name = "rateLimit")
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String ipKey = getClientIp(joinPoint) + ":" + joinPoint.getSignature().toString();
        Bucket bucket = resolveBucket(ipKey);

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        }

        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }

    private Object rateLimitFallback(ProceedingJoinPoint joinPoint, RateLimit rateLimit, Exception e) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable");
    }

    private Bucket resolveBucket(String key) {
        return proxyManager.builder().build(key, () ->
                BucketConfiguration.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(rateLimitProperties.getCapacity())
                                .refillGreedy(rateLimitProperties.getCapacity(), rateLimitProperties.getDuration())
                                .build())
                        .build()
        );
    }
}
