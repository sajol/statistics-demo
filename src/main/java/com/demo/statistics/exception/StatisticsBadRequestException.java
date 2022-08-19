package com.demo.statistics.exception;

public class StatisticsBadRequestException extends RuntimeException {
  public StatisticsBadRequestException(String message) {
    super(message);
  }

  public StatisticsBadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
