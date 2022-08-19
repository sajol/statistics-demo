package com.demo.statistics.service;

import com.demo.statistics.cache.StatisticsStore;
import com.demo.statistics.model.StatisticsAggregator;
import com.demo.statistics.model.Statistics;
import com.demo.statistics.model.StatisticsEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

import static com.demo.statistics.utils.TimeUtils.getTimestampInSecond;
import static com.demo.statistics.utils.TimeUtils.isEventTimeWithinPastSixtySeconds;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsStore statisticsStore;
    private final StatisticsAggregator statisticsAggregator;

    public StatisticsServiceImpl(StatisticsStore statisticsStore, StatisticsAggregator aggregator) {
        this.statisticsStore = statisticsStore;
        this.statisticsAggregator = aggregator;
    }

    @Override
    public boolean recordStatistics(StatisticsEvent event) {
        var currentTimeStampInSecond = getTimestampInSecond(Instant.now().toEpochMilli());
        var eventTimeStampInSecond = getTimestampInSecond(event.getTimestamp());
        if (!isEventTimeWithinPastSixtySeconds(currentTimeStampInSecond, eventTimeStampInSecond)) {
            return false;
        }
        var stats = statisticsStore.get(eventTimeStampInSecond);

        if (Objects.isNull(stats)) {
            synchronized (statisticsStore) {
                stats = statisticsStore.get(eventTimeStampInSecond);
                if (Objects.isNull(stats)) {
                    stats = new Statistics();
                    statisticsStore.put(eventTimeStampInSecond, stats);
                }
            }
        }
        stats.record(event);
        System.out.println("Recording statistics event " + event + "=>" + stats);
        return true;
    }


    @Override
    public String getStatistics() {
        return statisticsAggregator.getAggregatedStatistics(Instant.now().toEpochMilli());
    }
}
