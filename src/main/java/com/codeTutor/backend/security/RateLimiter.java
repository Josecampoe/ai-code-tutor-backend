package com.codeTutor.backend.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class RateLimiter {

    private final Map<String, RateLimitEntry> attempts = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxAttempts, long windowMs) {
        long now = System.currentTimeMillis();
        RateLimitEntry entry = attempts.computeIfAbsent(key, k -> new RateLimitEntry(now));

        synchronized (entry) {
            if (now - entry.windowStart > windowMs) {
                entry.windowStart = now;
                entry.count.set(1);
                return true;
            }
            if (entry.count.get() < maxAttempts) {
                entry.count.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private static class RateLimitEntry {
        long windowStart;
        AtomicInteger count;

        RateLimitEntry(long now) {
            this.windowStart = now;
            this.count = new AtomicInteger(1);
        }
    }
}
