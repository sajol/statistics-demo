package com.demo.statistics;

import com.demo.statistics.cache.StatisticsStore;
import com.demo.statistics.model.StatisticsEvent;
import com.demo.statistics.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StatisticsApplication.class)
public class StatisticsServiceTest {
    @Autowired
    StatisticsStore statisticsStore;
    @Autowired
    private StatisticsService statisticsService;

    private ExecutorService executorService;

    @Test
    public void contextLoads() {

        var store = new LinkedHashMap<Integer, Integer>();

        assertThat(statisticsService).isNotNull();
    }

    @BeforeEach
    public void beforeEachTask() {
        statisticsStore.clear();
        executorService = Executors.newFixedThreadPool(8);
    }

    public void shutDownExecutorService(long waitTime) {
        executorService.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                System.out.println("Going to terminate");
                // Cancel currently executing tasks
                executorService.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private StatisticsEvent createEvent(Long timestamp, double x, int y) {
        return new StatisticsEvent(timestamp, x, y);
    }

    @Test
    public void testRecordStatisticsWithEventTimestampNotWithinPastSixtySecond() {
        StatisticsEvent event = createEvent(Instant.now().toEpochMilli() - 60000, 0.0554600768, 2127711810);
        var added = statisticsService.recordStatistics(event);
        assertThat(added).isFalse();
    }

    @Test
    public void testRecordStatisticsWithEventTimestampWithinPastSixtySecond() {
        StatisticsEvent event = createEvent(Instant.now().toEpochMilli() - 10000, 0.0554600769, 2127711811);
        var added = statisticsService.recordStatistics(event);
        assertThat(added).isTrue();
    }

    @Test
    public void testRecordStatisticsWithEventTimestampThatWillLieWithinSameSlot() {
        var initialX = 0.0554600769;
        var initialY = 2127711811;
        var index = 0;
        var currentTimestamp = Instant.now().toEpochMilli();
        while (index < 60000) {
            var statisticsEvent = new StatisticsEvent(currentTimestamp, initialX + 1, initialY + 1);
            executorService.submit(() -> statisticsService.recordStatistics(statisticsEvent));
            currentTimestamp += 1;
            index++;
        }
        shutDownExecutorService(5);
        assertEquals(statisticsStore.size(), 1);
    }

    @Test
    public void testRecordStatisticsWithEventTimestampThatWillLieInDifferentSlot() {
        var initialX = 0.0554600769;
        var initialY = 2127711811;
        var index = 0;
        var currentTimestamp = Instant.now().toEpochMilli();
        while (index < 60) {
            var statisticsEvent = new StatisticsEvent(currentTimestamp, initialX + 1, initialY + 1);
            executorService.submit(() -> statisticsService.recordStatistics(statisticsEvent));
            currentTimestamp -= 1000;
            index++;
        }
        shutDownExecutorService(5);
        assertEquals(statisticsStore.size(), 60);
    }

    @Test
    public void testGetStatisticsWithNoData() {
        var stats = statisticsService.getStatistics();
        assertEquals("0,0.0000000000,0.0000000000,0,0.000", stats);
    }
}
