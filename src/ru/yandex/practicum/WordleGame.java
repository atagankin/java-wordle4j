package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import ru.yandex.practicum.exceptions.ImpossibleToFindHint;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;
import ru.yandex.practicum.exceptions.WordHasIncorrectLength;
import ru.yandex.practicum.exceptions.RepeatedAnswerException;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    public static final int DEFAULT_STEPS_COUNT = 6;
    public static final int DEFAULT_WORDS_LENGTH = 5;
    private static final Random rnd = new Random();

    private final PrintWriter logger;
    private String answer;
    private final Map<Character, Set<Integer>> answerStruct = new HashMap<>();

    private Set<Character> excludedChars = new HashSet<>(); // для букв, которых нет: маска -
    private Set<Character> includedChars = new HashSet<>(); // для букв, которых есть: маски +, ^
    private Map<Integer, Character> exectPositions = new HashMap<>();

    private Set<String> usedWords = new HashSet<>();

    private int steps;
    private int stepsLeft;
    private int hintLeft;

    public boolean isWin = false;

    private final WordleDictionary dictionary;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.steps = DEFAULT_STEPS_COUNT;
        this.stepsLeft = DEFAULT_STEPS_COUNT;
        this.dictionary = dictionary;
        this.logger = logger;
        this.hintLeft = 3;
    }

    public String getAnswer() {
        return answer;
    }

    public Map<Character, Set<Integer>> getAnswerStruct() {
        return answerStruct;
    }

    public int getSteps() {
        return steps;
    }

    public int getHintLeft() {
        return hintLeft;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public void chooseTargetWord() {
        this.answer = dictionary.getRandomWord();
        // Формируем мапу с индексами букв для простоты создания паттерна результата
        setAnswerStruct();
    }

    public void chooseTargetWord(int index) {
        this.answer = dictionary.getRandomWord(index);
        // Формируем мапу с индексами букв для простоты создания паттерна результата
        setAnswerStruct();
    }

    private void setAnswerStruct() {
        for (int i = 0; i < this.answer.length(); i++) {
            Character ch = this.answer.charAt(i);
            Set<Integer> charPositions = this.answerStruct.getOrDefault(ch, new HashSet<>());
            charPositions.add(i);
            this.answerStruct.put(ch, charPositions);
        }
    }

    // Формируем паттерн ответа
    // Все проверки имеют алгоритмическую сложность O(1)
    // Кроме анализа пользовательского ответа, в нем O(N), но там константное кол-во букв
    public String getStepPattern(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            Character chr = word.charAt(i);
            // Проверяем, ели ли такой символ в загаданном слове
            if (answerStruct.containsKey(chr)) {
                // Проверяем, на нужной ли позиции находится буква
                boolean isOnPlace = answerStruct.get(chr).contains(i);
                if (isOnPlace) {
                    sb.append("+");
                } else {
                    sb.append("^");
                }
            } else {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    // Содержит хотя бы одну букву из неправильных
    private boolean containsExcludedChars(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (excludedChars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    // Содержит ВСЕ буквы из правильных
    // Можно подумать над: содержит ВСЕ на конкретных позициях и хотя бы одну из возможных
    private boolean containsIncludedChars(String word) {
        for (char c: includedChars) {
            if (word.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    // Проверяем буквы, которые должны быть на конкретных позициях
    private boolean isOnTheRightPlace(String word) {
        for (Map.Entry<Integer, Character> entry: exectPositions.entrySet()) {
            Character checkChar = entry.getValue();
            int checkPosition = entry.getKey();

            if (word.charAt(checkPosition) != checkChar) {
                return false;
            }
        }
        return true;
    }

    public List<String> getHintList() throws ImpossibleToFindHint {
        List<String> allWords = this.dictionary.getWords();

        List<String> hints = allWords.stream()
                .filter(word -> !containsExcludedChars(word))
                .filter(word -> containsIncludedChars(word))
                .filter(word -> isOnTheRightPlace(word))
                .filter(word -> !usedWords.contains(word))
                .filter(word -> !word.equals(answer))
                .collect(Collectors.toList());

        if (hints.isEmpty()) {
            logger.println("[GameError]: Невозможно подобрать подсказку, осталось только загаданное слово.");
            throw new ImpossibleToFindHint("[GameError]: Невозможно подобрать подсказку, осталось только загаданное слово.");
        }

        return hints;
    }

    // Выбираем случайное слово из возможных подсказок
    public String getHint() throws ImpossibleToFindHint {
        if (this.includedChars.isEmpty()) {
            this.includedChars.add(answer.charAt(0));
            this.exectPositions.put(0, answer.charAt(0));
        }

        if (this.hintLeft == 0) {
            throw new ImpossibleToFindHint("Подсказки закончились.");
        }

        List<String> hints = getHintList();
        this.hintLeft--;
        return hints.get(rnd.nextInt(hints.size()));
    }

    public void updateConstraints(String word, String pattern) {
        usedWords.add(word);
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            char positionType = pattern.charAt(i);

            switch (positionType) {
                case '+':
                    this.includedChars.add(letter);
                    this.exectPositions.put(i, letter);
                    break;
                case '^':
                    this.includedChars.add(letter);
                    break;
                case '-':
                    this.excludedChars.add(letter);
                    break;
                default:
                    System.out.println("Ошибка маски");
                    break;
            }
        }
    }

    public StepResult makeStep(String word) throws ImpossibleToFindHint, WordNotFoundInDictionary,
            WordHasIncorrectLength, RepeatedAnswerException {
        logger.println("Попытка игрока. Осталось попыток: " + stepsLeft);
        boolean isHint = false;

        if (word == null || word.trim().isEmpty()) {
            isHint = true;
            word = getHint();
            logger.println("Запрос подсказки. Подсказка : " + word);
        } else {
            word = WordleDictionary.normalize(word);
            if (word.length() != DEFAULT_WORDS_LENGTH) {
                logger.println("[ERROR] Неверная длина слова. Игрок ввел: " + word);
                throw new WordHasIncorrectLength("Слово должно состоять из " + DEFAULT_WORDS_LENGTH + " букв");
            }

            if (!dictionary.contains(word)) {
                logger.println("[ERROR] Введенного слова нет в словаре. Игрок ввел: " + word);
                throw new WordNotFoundInDictionary(
                        "Слово " + word + "отсутствует в словаре. Угадать нужно слово только из загруженного словаря.");
            }

            if (usedWords.contains(word)) {
                logger.println("[ERROR] Игрок повторно ввел слово: " + word);
                throw new RepeatedAnswerException("Слово уже было использовано : " + word);
            }
            logger.println("Игрок ввел : " + word);
        }

        stepsLeft--;
        String pattern = getStepPattern(word);
        updateConstraints(word, pattern);

        logger.println("Результат хода: " + word + " -> " + pattern);

        return new StepResult(word, pattern, isHint, this.hintLeft);
    }

    public int getStepsLeft() {
        return stepsLeft;
    }
}

