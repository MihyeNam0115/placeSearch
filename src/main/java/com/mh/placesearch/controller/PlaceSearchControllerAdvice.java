package com.mh.placesearch.controller;

import com.mh.placesearch.controller.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class PlaceSearchControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(Exception e) {
        final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code("IllegalArgumentException")
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> exceptionAllHandler(Exception e) {
        final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .code("IllegalArgumentException")
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }
}
