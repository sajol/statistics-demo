package com.demo.statistics.model;


import com.demo.statistics.exception.StatisticsBadRequestException;
import lombok.Data;


@Data
public class StatisticsEvent {
    private final long timestamp;
    private final double x;
    private final int y;

    public static StatisticsEvent from(String commaSeparatedEvent) {
        var split = commaSeparatedEvent.split(",");
        if (split.length < 3) {
            throw new StatisticsBadRequestException("Statistics event does not contain all the needed information");
        }
        try {
            return new StatisticsEvent(Long.parseLong(split[0]), Double.parseDouble(split[1]), Integer.parseInt(split[2]));
        } catch (Exception ex) {
            throw new StatisticsBadRequestException("Statistics even string is not properly formatted");
        }
    }
}
