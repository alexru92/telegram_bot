package com.example.ApartmentsSearchWithTelegramReporting;

import java.io.*;
import java.util.function.Function;

public class FileUtils {

    private static void ensureFileExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T readFromFile(String fileName, Function<BufferedReader, T> processor) {
        ensureFileExists(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return processor.apply(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeToFile(String fileName, String line, boolean append) {
        ensureFileExists(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void emptyFile(String fileName) {
        ensureFileExists(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}