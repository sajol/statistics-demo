package com.demo.statistics.model;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Statistics {

    private long total;
    private BigDecimal sumX = new BigDecimal("0.0");
    private BigDecimal avgX = new BigDecimal("0.0");
    private BigInteger sumY = BigInteger.ZERO;
    private BigDecimal avgY = new BigDecimal("0.0");

    public synchronized void record(StatisticsEvent statisticsEvent) {
        total += 1;
        sumX = sumX.add(BigDecimal.valueOf(statisticsEvent.getX())).setScale(10, RoundingMode.HALF_UP);
        avgX = sumX.divide(BigDecimal.valueOf(total), RoundingMode.HALF_UP).setScale(10, RoundingMode.HALF_UP);
        sumY = sumY.add(BigInteger.valueOf(statisticsEvent.getY()));
        avgY = BigDecimal.valueOf(sumY.longValue()).divide(BigDecimal.valueOf(total), RoundingMode.HALF_UP).setScale(3, RoundingMode.HALF_UP);
    }

    public synchronized Statistics copy() {
        return Statistics.builder()
                .total(total)
                .sumX(sumX)
                .avgX(avgX)
                .sumY(sumY)
                .avgY(avgY)
                .build();
    }
}
