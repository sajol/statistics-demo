package com.demo.statistics.service;

import com.demo.statistics.model.StatisticsEvent;

public interface StatisticsService {

    boolean recordStatistics(StatisticsEvent event);

    String getStatistics();
}
