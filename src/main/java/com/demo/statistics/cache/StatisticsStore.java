package com.demo.statistics.cache;

import com.demo.statistics.model.Statistics;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StatisticsStore extends LinkedHashMap<Long, Statistics> {

    private static final int CACHE_SIZE = 60; // past 60 seconds

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Statistics> eldest) {
        return size() > CACHE_SIZE;
    }
}
