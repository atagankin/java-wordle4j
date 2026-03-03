package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionary extends GameException {
    public WordNotFoundInDictionary(String message) {
        super(message);
    }
}
