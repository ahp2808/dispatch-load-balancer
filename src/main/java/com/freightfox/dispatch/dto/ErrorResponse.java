package com.freightfox.dispatch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String status;
    private String message;
    private List<String> errors;
    private String timestamp;

    public ErrorResponse() {
        this.status = "error";
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(String message) {
        this.status = "error";
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(String message, List<String> errors) {
        this.status = "error";
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now().toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
