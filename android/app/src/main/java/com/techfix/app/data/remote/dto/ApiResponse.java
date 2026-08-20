package com.techfix.app.data.remote.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getTimestamp() { return timestamp; }
}
