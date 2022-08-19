package com.demo.statistics.model;

import com.demo.statistics.cache.StatisticsStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.statistics.utils.TimeUtils.getTimestampInSecond;
import static com.demo.statistics.utils.TimeUtils.isEventTimeWithinPastSixtySeconds;

@Component
public class StatisticsAggregator {

    private final StatisticsStore statisticsStore;

    public StatisticsAggregator(StatisticsStore statisticsStore) {
        this.statisticsStore = statisticsStore;
    }


    public String getAggregatedStatistics(Long currentTimestamp) {
        long currentTimeInSecond = getTimestampInSecond(currentTimestamp);
        var stats = statisticsStore.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));

        var total = 0L;
        var sumX = new BigDecimal("0.0");
        var avgX = new BigDecimal("0.0");
        var sumY = BigInteger.ZERO;
        var avgY = new BigDecimal("0.0");

        for (var stat : stats.entrySet()) {
            var eventTimestampInSecond = stat.getKey();
            var statistics = stat.getValue();
            if (isEventTimeWithinPastSixtySeconds(currentTimeInSecond, eventTimestampInSecond)) {
                total += 1;
                sumX = sumX.add(statistics.getSumX()).setScale(10, RoundingMode.HALF_UP);
                sumY = sumY.add(statistics.getSumY());
            }
        }
        if (total > 0) {
            avgX = sumX.divide(BigDecimal.valueOf(total), RoundingMode.HALF_UP).setScale(10, RoundingMode.HALF_UP);
            avgY = BigDecimal.valueOf(sumY.longValue()).divide(BigDecimal.valueOf(total), RoundingMode.HALF_UP).setScale(10, RoundingMode.HALF_UP);
        }
        return String.format("%d,%.10f,%.10f,%d,%.3f", total, sumX.doubleValue(), avgX.doubleValue(), sumY.longValue(), avgY.doubleValue());
    }
}
