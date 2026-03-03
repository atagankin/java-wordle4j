package ru.yandex.practicum;

public record StepResult(
    String word,
    String pattern,
    boolean isHint,
    int hintLeft
) {}
