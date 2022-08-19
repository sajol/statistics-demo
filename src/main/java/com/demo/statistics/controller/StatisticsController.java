package com.demo.statistics.controller;

import com.demo.statistics.model.StatisticsEvent;
import com.demo.statistics.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("/event")
    public ResponseEntity<Object> recordStatistics(@RequestBody String event) {
        var recorded = statisticsService.recordStatistics(StatisticsEvent.from(event));
        if (recorded) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        return new ResponseEntity<>(statisticsService.getStatistics(), HttpStatus.OK);
    }
}
