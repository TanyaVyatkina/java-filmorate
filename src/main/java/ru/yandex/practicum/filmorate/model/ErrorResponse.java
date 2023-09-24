package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String error;

    private final String stack;

    public ErrorResponse(String error, String stack) {
        this.error = error;
        this.stack = stack;
    }

    public String getError() {
        return error;
    }

    public String getStack() {
        return stack;
    }
}