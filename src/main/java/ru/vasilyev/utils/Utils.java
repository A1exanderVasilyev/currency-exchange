package ru.vasilyev.utils;

public class Utils {

    public static String getCurrencyCodeFromPath(String path) {
        int codeLength = 3;
        return path.substring(1, codeLength + 1).toUpperCase();
    }
}
