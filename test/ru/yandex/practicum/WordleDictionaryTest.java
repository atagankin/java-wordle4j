package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

public class WordleDictionaryTest {
    private WordleDictionary dictionary;

    @BeforeEach
    void init() {
        dictionary = new WordleDictionary();
        List<String> words = new ArrayList<>(List.of("кошка", "собака", "дом", "кот"));
        dictionary.setWords(words);
    }

    @Test
    void testNormalize() {
        assertEquals("ежик", WordleDictionary.normalize("ёжик"));
        assertEquals("дом", WordleDictionary.normalize("ДОМ"));
        assertEquals("лес", WordleDictionary.normalize(" лес "));
    }

    @Test
    void testContains() {
        assertTrue(dictionary.contains("кошка"));
        assertTrue(dictionary.contains("КОШКА"));
        assertTrue(dictionary.contains(" кошка "));
        assertFalse(dictionary.contains("машина"));
    }

}
