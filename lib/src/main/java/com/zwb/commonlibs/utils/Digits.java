package com.zwb.commonlibs.utils;

public class Digits {
    private static final char[] lowerCaseDigits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final char[] upperCaseDigits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static String bytesToHex(byte[] bytes, boolean upperCase) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        char[] digits = upperCase ? upperCaseDigits : lowerCaseDigits;
        for (byte b : bytes) {
            sb.append(digits[(b & 0xF0) >> 4]).append(digits[b & 0xF]);
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] bytes, boolean upperCase, String delimiter) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        char[] digits = upperCase ? upperCaseDigits : lowerCaseDigits;
        for (byte b : bytes) {
            sb.append(digits[(b & 0xF0) >> 4]).append(digits[b & 0xF]).append(delimiter);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - delimiter.length());
        }
        return sb.toString();
    }
}
