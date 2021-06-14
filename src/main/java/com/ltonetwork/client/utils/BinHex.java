package com.ltonetwork.client.utils;

import static java.lang.Character.digit;

public class BinHex {
    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();

    /**
     * @param bytes bytes
     * @return
     */
    public static String bin2hex(byte... bytes) {
        if (bytes == null) return null;
        char[] hex = new char[bytes.length * 2];
        int counter = 0;
        for (byte b : bytes) {
            hex[counter++] = ALPHABET[(b & 0xff) >> 4];
            hex[counter++] = ALPHABET[(b & 0xff) & 0xf];
        }
        return new String(hex);
    }

    /**
     * @param hexString hexString all lower case, 0-9 and a-f only, even length only, null safe
     * @return
     */
    public static byte[] hex2bin(String hexString) {
        if (hexString == null) return null;
        int len = hexString.length();
        byte[] bin = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            bin[i >> 1] = (byte) ((digit((int) hexString.charAt(i), 16) << 4) + digit((int) hexString.charAt(i + 1), 16));
        }
        return bin;
    }
}
