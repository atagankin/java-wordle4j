package ru.yandex.practicum.exceptions;

public abstract class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}
