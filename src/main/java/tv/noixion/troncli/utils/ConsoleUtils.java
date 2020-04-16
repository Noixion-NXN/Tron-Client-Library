package tv.noixion.troncli.utils;

import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

/**
 * Utils for console client.
 */
public class ConsoleUtils {
    private static ConsoleReader reader = null;

    /**
     * Reads a password
     *
     * @param promt Promt to show
     * @return The password read.
     */
    public static String readPassword(String promt) {
        ConsoleReader reader;

        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            if (System.console() != null) {
                System.out.print(promt);
                return new String(System.console().readPassword());
            } else {
                Scanner sc = new Scanner(System.in);
                System.out.print(promt);
                if (sc.hasNext()) {
                    return sc.nextLine();
                } else {
                    return "";
                }
            }
        }

        try {
            return reader.readLine(promt, '*');
        } catch (IOException ex) {
            return "";
        }
    }

    /**
     * Reads a text file.
     *
     * @param file the file to read.
     * @return The content of the file.
     * @throws IOException
     */
    public static String readTextFile(File file) throws IOException {
        String content = "";
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            content += line + "\n";
        }
        return content;
    }

    /**
     * Turns a list into string to show it.
     *
     * @param list The list
     * @return The list as string.
     */
    public static String listToString(List<String> list) {
        boolean first = true;
        String result = "";
        for (String e : list) {
            if (first) {
                first = false;
            } else {
                result += ", ";
            }
            result += e;
        }
        return result;
    }

    /**
     * Asks the user for confirmation
     *
     * @return true if the user confirmad, false if the user cancelled.
     */
    public static boolean askConfirmation() {
        ConsoleReader reader;
        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            return false;
        }
        String resp;
        try {
            resp = reader.readLine("Confirm? (Y/N): ");
        } catch (IOException ex) {
            return false;
        }
        return resp.trim().equalsIgnoreCase("y");
    }


    public static String readString(String prompt) {
        ConsoleReader reader;
        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            return "";
        }
        String resp;
        try {
            resp = reader.readLine(prompt);
        } catch (IOException ex) {
            return "";
        }
        return resp;
    }
}
