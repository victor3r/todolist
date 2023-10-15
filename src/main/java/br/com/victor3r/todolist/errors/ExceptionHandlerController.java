package br.com.victor3r.todolist.errors;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<HashMap<String, String>> handleResponseStatusException(ResponseStatusException e) {
    var hashMap = new HashMap<String, String>();

    hashMap.put("message", e.getReason());

    return ResponseEntity.status(e.getStatusCode()).body(hashMap);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<HashMap<String, String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    var hashMap = new HashMap<String, String>();

    hashMap.put("message", e.getMostSpecificCause().getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(hashMap);
  }
}
