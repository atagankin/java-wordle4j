package ru.yandex.practicum;

import java.util.*;


/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private static final Random rnd = new Random();

    private List<String> words;
    private final Set<String> wordsCache;

    public WordleDictionary() {
        this.words = new ArrayList<>();
        this.wordsCache = new HashSet<>();
    }

    public void setWords(List<String> words) {
        this.words = words;
        this.wordsCache.addAll(this.words);
    }

    public static String normalize(String word) {
        return word.toLowerCase().replace("ё", "е").trim();
    }

    public boolean contains(String word) {
        return wordsCache.contains(WordleDictionary.normalize(word));
    }

    public String getRandomWord() {
        return words.get(rnd.nextInt(words.size()));
    }

    // Второй способ определения целевого слова (по индексу)
    // Планируется для тестов
    public String getRandomWord(int index) {
        return words.get(index);
    }

    public List<String> getWords() {
        return words;
    }
}
