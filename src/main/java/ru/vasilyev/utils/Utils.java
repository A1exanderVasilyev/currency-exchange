package ru.vasilyev.utils;

public class Utils {
    private static final int CODE_LENGTH = 3;
    public static String getCurrencyCodeFromPath(String path, int startIndex, int endIndex) {
        return path.substring(startIndex, endIndex).toUpperCase();
    }

    public static String[] getCurrencyPairFromPath(String path) {
        int arrSize = 2;
        String[] result = new String[arrSize];
        result[0] = getCurrencyCodeFromPath(path, 1, CODE_LENGTH + 1);
        result[1] = getCurrencyCodeFromPath(path, CODE_LENGTH + 1, CODE_LENGTH * 2 + 1);
        return result;
    }

    public static boolean isAnyParamsEmpty(String[] args) {
        for (String arg : args) {
            if (arg == null || arg.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
