package com.ecommerce.advance.product.saga;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SagaTracker {
    private final Map<Long, Integer> confirmations = new ConcurrentHashMap<>();

    public void confirm(Long pid) {
        confirmations.merge(pid, 1, Integer::sum);
    }

    public boolean isAllConfirmed(Long pid) {
        return confirmations.getOrDefault(pid, 0) >= 2;
    } // price + detail

    public void clear(Long pid) {
        confirmations.remove(pid);
    }

}
