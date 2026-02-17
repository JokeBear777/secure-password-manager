package com.InfoSec.dynamic_password.global.exeption;

import com.InfoSec.dynamic_password.global.exeption.custom.OAuthSignupSessionNotFoundException;
import com.InfoSec.dynamic_password.global.exeption.custom.RedisOperationException;
import com.InfoSec.dynamic_password.global.utils.dto.MessageStatusResponseDto;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.webjars.NotFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerGlobalException(Exception e) {
        MessageStatusResponseDto messageStatusResponseDto = new MessageStatusResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageStatusResponseDto);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<?> handlerRedisOperationException(RedisOperationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Redis Error" + e.getMessage());
    }

    @ExceptionHandler(OAuthSignupSessionNotFoundException.class)
    public ResponseEntity<?> handleOAuthSessionException(
            OAuthSignupSessionNotFoundException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
    }

}
