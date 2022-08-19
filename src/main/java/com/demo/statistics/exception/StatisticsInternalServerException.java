package com.demo.statistics.exception;

public class StatisticsInternalServerException extends RuntimeException {
  public StatisticsInternalServerException(String message) {
    super(message);
  }

  public StatisticsInternalServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
