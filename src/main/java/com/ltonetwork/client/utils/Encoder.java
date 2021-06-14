package com.ltonetwork.client.utils;

import com.ltonetwork.client.exceptions.InvalidArgumentException;
import com.ltonetwork.client.utils.Base58;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Encoder {
    public static String base58Encode(String string, String charset) {
        try {
            return Base58.encode(string.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String base58Encode(String string) {
        return base58Encode(string, "UTF-8");
    }

    public static String base58Encode(byte[] string) {
        try {
            if (string == null) {
                return "";
            }
            return Base58.encode(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String base58Decode(String string, Charset charset) {
        return new String(Base58.decode(string), charset);
    }

    public static byte[] base58Decode(String string) {
        return Base58.decode(string);
    }

    public static String base64Encode(String input) {
        try {
            return new String(Base64.getEncoder().encode(input.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64Encode(byte[] input) {
        try {
            return new String(Base64.getEncoder().encode(input));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] base64Decode(String input) {
        return Base64.getDecoder().decode(input);
    }

    public static byte[] base64Decode(byte[] input) {
        return Base64.getDecoder().decode(input);
    }

    public static String base64Decode(String input, Charset charset) {
        return new String(Base64.getDecoder().decode(input), charset);
    }

    public static String base64Decode(byte[] input, Charset charset) {
        return new String(Base64.getDecoder().decode(input), charset);
    }

    public static String hexEncode(String input) {
        return Hex.encodeHexString(input.getBytes());
    }

    public static String hexDecode(String input) {
        try {
            return new String(Hex.decodeHex(input));
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fromBase58StringToXString(String input, String toEncoding) {
        return switch (toEncoding) {
            case "base58" -> input;
            case "base64" -> base64Encode(base58Decode(input));
            case "raw" -> base58Decode(input, StandardCharsets.UTF_8);
            case "hex" -> hexEncode(base58Decode(input, StandardCharsets.UTF_8));
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", toEncoding));
        };
    }

    public static String fromXStringToBase58String(String input, String fromEncoding) {
        return switch (fromEncoding) {
            case "base58" -> input;
            case "base64" -> base58Encode(base64Decode(input));
            case "raw" -> base58Encode(input);
            case "hex" -> base58Encode(hexDecode(input));
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", fromEncoding));
        };
    }

    public static String decode(String input, String encoding) {
        return switch (encoding) {
            case "base58" -> base58Decode(input, StandardCharsets.UTF_8);
            case "base64" -> base64Decode(input, StandardCharsets.UTF_8);
            case "raw" -> input;
            case "hex" -> hexDecode(input);
            default -> throw new InvalidArgumentException(String.format("Failed to encode to %s", encoding));
        };
    }

    public static String repeat(String string, int times) {
        return new String(new char[times]).replace("\0", string);
    }

    static byte[] packN(int value) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        bytes = toPositiveByteArray(bytes);
        return bytes;
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
