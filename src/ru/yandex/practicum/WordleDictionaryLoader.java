package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import ru.yandex.practicum.exceptions.*;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final PrintWriter logger;

    public WordleDictionaryLoader(PrintWriter logger) {
        this.logger = logger;
    }

    public WordleDictionary loadDictionary(String filename) throws FileDictionaryNotFound,DictionaryFileIsEmpty,IOException {

        WordleDictionary dict = new WordleDictionary();
        List<String> wordList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String word = br.readLine();
                if (!word.isEmpty() && word.length() == WordleGame.DEFAULT_WORDS_LENGTH) {
                    wordList.add(WordleDictionary.normalize(word));
                }
            }

            if (wordList.isEmpty()) {
                logger.println("Файл словаря пуст или не содержит слов нужной длины");
                throw new DictionaryFileIsEmpty(
                        "Файл словаря (" + filename + ") пуст или не содержит слов длиной " +
                        WordleGame.DEFAULT_WORDS_LENGTH + " букв");
            }

            dict.setWords(wordList);
            this.logger.println("Загружено "+ wordList.size() +" слов");
            return dict;
        } catch(FileNotFoundException e) {
            this.logger.println("Файл словаря не найден: " + filename);
            throw new FileDictionaryNotFound("[CRITICAL] Файл словаря (" + filename + ") не найден");
        }  catch (IOException e) {
            this.logger.println("Ошибка чтения файла: " + e.getMessage());
            throw new IOException("[CRITICAL] Ошибка загрузки словаря: " + e.getMessage());
        }
    }

}
