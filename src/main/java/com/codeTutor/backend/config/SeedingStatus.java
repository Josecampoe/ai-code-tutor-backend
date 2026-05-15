package com.codeTutor.backend.config;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

@Component
public class SeedingStatus {
    private final AtomicBoolean complete = new AtomicBoolean(true);

    public boolean isComplete() {
        return complete.get();
    }

    public void markComplete() {
        complete.set(true);
    }
}
