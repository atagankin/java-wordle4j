package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.ImpossibleToFindHint;
import ru.yandex.practicum.exceptions.WordHasIncorrectLength;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;
import ru.yandex.practicum.exceptions.RepeatedAnswerException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    PrintWriter logger;
    WordleDictionaryLoader loader;
    WordleDictionary dictionary;
    WordleGame game;
    final String dictionaryTestFile = "test_dict.txt";

    @BeforeEach
    void init() {
        try (FileWriter writer = new FileWriter(dictionaryTestFile, StandardCharsets.UTF_8)) {
            writer.write("гонец\n");
            writer.write("горец\n");
        } catch (IOException e) {
            fail("Невозможно создать словарь");
        }

        logger = new PrintWriter(System.err);
        loader = new WordleDictionaryLoader(logger);
        try {
            dictionary = loader.loadDictionary(dictionaryTestFile);
            game = new WordleGame(dictionary, logger);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    void close() {
        File testDictFile = new File(dictionaryTestFile);
        testDictFile.delete();
    }

    @Test
    void testStepIsDecreased() {
        int beforeSteps = game.getStepsLeft();
        try {
            StepResult result = game.makeStep("горец");
            int afterSteps = game.getStepsLeft();
            assertEquals((beforeSteps - 1), afterSteps);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testWordNotInDictionary() {
        assertThrows(WordNotFoundInDictionary.class, () -> {
           StepResult result = game.makeStep("кошка");
        });
    }

    @Test
    void testWordWrongLength() {
        assertThrows(WordHasIncorrectLength.class, () -> {
            StepResult result = game.makeStep("дом");
        });
    }

    @Test
    void testGetHint() {
        try {
            game.chooseTargetWord();
            StepResult result = game.makeStep("");
            assertNotNull(result.word());
            assertTrue(result.isHint());
        } catch (ImpossibleToFindHint e) {
            fail("Не нашел подсказку, а должен!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testFailToGetHint() {
        game.chooseTargetWord();
        assertThrows(ImpossibleToFindHint.class, () -> {
           game.chooseTargetWord();
            for (int i = 0; i < 2; i++) {
                StepResult result =  game.makeStep("");
            }
        });
    }

    @Test
    void testHintValueIsCorrect() {
        game.chooseTargetWord(0);
        try {
            StepResult result = game.makeStep("");
            assertEquals("горец", result.word());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testReapetedAnswer() {
        game.chooseTargetWord(0);
        assertThrows(RepeatedAnswerException.class, () -> {
            game.chooseTargetWord();
            for (int i = 0; i < 2; i++) {
                StepResult result =  game.makeStep("горец");
            }
        });
    }

}
