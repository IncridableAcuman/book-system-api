package com.app.backend.exception;

import com.app.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, HttpServletRequest request){
        String safeMessage = HtmlUtils.htmlEscape(e.getMessage());
        String safeUri = HtmlUtils.htmlEscape(request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
               status.value(),
                status.getReasonPhrase(),
                safeMessage,
                safeUri,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException e, HttpServletRequest request){
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException e,HttpServletRequest request){
        return buildErrorResponse(e,HttpStatus.NOT_FOUND,request);
    }
    @ExceptionHandler(UnauthorizeException.class)
    public ResponseEntity<ErrorResponse> unAuthorize(UnauthorizeException e,HttpServletRequest request){
        return buildErrorResponse(e,HttpStatus.UNAUTHORIZED,request);
    }
}
