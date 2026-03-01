package ru.yandex.practicum.exceptions;

public class DictionaryFileIsEmpty extends RuntimeException {
    public DictionaryFileIsEmpty(String message) {
        super(message);
    }
}
