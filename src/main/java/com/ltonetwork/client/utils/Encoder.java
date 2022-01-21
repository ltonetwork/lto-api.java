package com.ltonetwork.client.utils;

import com.ltonetwork.client.types.Encoding;
import com.ltonetwork.seasalt.Binary;
import org.apache.commons.codec.DecoderException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Encoder {

    public static String base58Encode(byte[] input) {
        return new Binary(input).getBase58();
    }

    public static String base58Encode(String input) {
        return base58Encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] base58Decode(String input) {
        return Binary.fromBase58(input).getBytes();
    }

    public static byte[] base58Decode(byte[] input) {
        return base58Decode(new String(input, StandardCharsets.UTF_8));
    }

    public static String base64Encode(byte[] input) {
        return new Binary(input).getBase64();
    }

    public static String base64Encode(String input) {
        return base64Encode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] base64Decode(String input) {
        return Binary.fromBase64(input).getBytes();
    }

    public static byte[] base64Decode(byte[] input) {
        return base64Decode(new String(input, StandardCharsets.UTF_8));
    }

    public static String hexEncode(byte[] input) {
        return new Binary(input).getHex();
    }

    public static String hexEncode(String input) {
        return hexEncode(input.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hexDecode(String input) {
        try {
            return Binary.fromHex(input).getBytes();
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Unable to decode hex string " + input);
        }
    }

    public static byte[] hexDecode(byte[] input) {
        return hexDecode(new String(input, StandardCharsets.UTF_8));
    }

    public static byte[] decode(String input, Encoding encoding) {
        switch (encoding) {
            case BASE58:
                return base58Decode(input);
            case BASE64:
                return base64Decode(input);
            case RAW:
                return input.getBytes(StandardCharsets.UTF_8);
            case HEX:
                return hexDecode(input);
            default:
                throw new IllegalArgumentException("Unknown encoding");
        }
    }

    public static byte[] decode(byte[] input, Encoding encoding) {
        return decode(new String(input, StandardCharsets.UTF_8), encoding);
    }

    public static String encode(byte[] input, Encoding encoding) {
        switch (encoding) {
            case BASE58:
                return base58Encode(input);
            case BASE64:
                return base64Encode(input);
            case RAW:
                return new String(input, StandardCharsets.UTF_8);
            case HEX:
                return hexEncode(input);
            default:
                throw new IllegalArgumentException("Unknown encoding");
        }
    }

    public static String encode(String input, Encoding encoding) {
        return encode(input.getBytes(StandardCharsets.UTF_8), encoding);
    }

    public static boolean isBase58Encoded(String input) {
        return Pattern.matches("^[1-9A-HJ-NP-Za-km-z]+$", input);
    }

    public static boolean isBase64Encoded(String input) {
        return Pattern.matches("^[A-Za-z0-9+/]+={0,2}$", input);
    }

    public static boolean isHexEncoded(String input) {
        return Pattern.matches("^[a-f0-9]+$", input);
    }

    static byte[] packN(int value) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        return toPositiveByteArray(bytes);
    }

    static int unpackN(byte[] value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(value);
        buf.flip();
        return buf.getInt();
    }

    // converts a byte[] like [0,0,19,-2] to [0,0,19,254]
    public static byte[] toPositiveByteArray(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] < 0 ? bytes[i] + 256 : bytes[i]);
        }
        return bytes;
    }
}
