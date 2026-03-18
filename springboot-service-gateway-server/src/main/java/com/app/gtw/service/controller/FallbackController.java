package com.app.gtw.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

  @GetMapping("/orders")
  public ResponseEntity<Map<String, String>> ordersFallback() {
    return ResponseEntity
    .status(HttpStatus.SERVICE_UNAVAILABLE)
    .body(Map.of(
    "status", "error",
    "message", "Service is unavailable, please contact administrator."
    ));
  }

}
