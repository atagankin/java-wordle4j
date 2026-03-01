package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import ru.yandex.practicum.exceptions.*;
import java.util.Scanner;


/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(new FileWriter("log.log"), true)) {
            logger.println("Поехали!");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
            WordleGame game = new WordleGame(dictionary, logger);
            game.chooseTargetWord();
            logger.println("Загаданное слово : " + game.getAnswer());

            System.out.println("Загадано слово из " + WordleGame.DEFAULT_WORDS_LENGTH + " букв.");
            System.out.println("У вас " + WordleGame.DEFAULT_STEPS_COUNT + " попыток.");
            System.out.println("* Пустая строка - запрос подсказки.");


            try (Scanner scanner = new Scanner(System.in)) {
                while (!game.isWin && game.getStepsLeft() > 0) {
                    System.out.println("Осталось попыток: " + game.getStepsLeft());
                    System.out.print("Введите слово: ");
                    String input = scanner.nextLine();

                    if (WordleDictionary.normalize(input).equals(game.getAnswer())) {
                        System.out.println("ПОБЕДА!!!\nВы угадали слово!");
                        game.setWin(true);
                        logger.println("Игра окончена. Игрок победил!");
                        break;
                    } else {
                        try {
                            StepResult stepResult = game.makeStep(input);
                            if (stepResult.isHint()) {
                                System.out.println("Подсказка: " + stepResult.word());
                            }
                            System.out.println("Результат: " + stepResult.pattern());
                        } catch (WordNotFoundInDictionary | WordHasIncorrectLength | RepeatedAnswerException e) {
                            System.out.println(e.getMessage());
                        } catch (ImpossibleToFindHint e) {
                            System.out.println(e.getMessage());
                            System.out.println("Попробуйте угадать слово самостоятельно!");
                            logger.println("Критическая ошибка подсказки: " + e.getMessage());
                        }
                    }
                }
            }

            if (!game.isWin) {
                System.out.println("Игра окончена! Загаданное слово: " + game.getAnswer());
                logger.println("Игра проиграна!");
            }
        } catch (FileDictionaryNotFound | DictionaryFileIsEmpty e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Невозможно создать лог файл: " + e.getMessage());
        }
    }
}
