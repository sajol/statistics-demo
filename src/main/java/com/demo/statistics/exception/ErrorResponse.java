package com.demo.statistics.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  @JsonProperty("error_message")
  private final String message;

  @JsonProperty("error_code")
  private final String code;
}
