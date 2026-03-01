package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.FileDictionaryNotFound;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    private PrintWriter logger;
    private WordleDictionaryLoader loader;

    @BeforeEach
    void init() {
        logger = new PrintWriter(System.err);
        loader = new WordleDictionaryLoader(logger);
    }

    @Test
    void testLoadDictionaryNotFound() {
        assertThrows(FileDictionaryNotFound.class, () -> {
            loader.loadDictionary("nonexistent.txt");
        });
    }

    @Test
    void testLoadGameDictionaryBy5Letters() {
        try {
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
            assertEquals(4159, dictionary.getWords().size());
        } catch (IOException e) {
            fail("Ошибка загрузки словаря");
        }

    }


}
