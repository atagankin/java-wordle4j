package ru.yandex.practicum.exceptions;

public class FileDictionaryNotFound extends RuntimeException {
    public FileDictionaryNotFound(String message) {
        super(message);
    }
}
