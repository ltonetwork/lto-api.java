package com.ltonetwork.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PackUtil {

    /**
     * Packing string
     * SimilarphpinpackstayjavaImplementation in
     *
     * @param str
     * @return
     */
    public static byte[] pack(String str) {
        int nibbleshift = 4;
        int position = 0;
        int len = str.length() / 2 + str.length() % 2;
        byte[] output = new byte[len];
        for (char v : str.toCharArray()) {
            byte n = (byte) v;
            if (n >= '0' && n <= '9') {
                n -= '0';
            } else if (n >= 'A' && n <= 'F') {
                n -= ('A' - 10);
            } else if (n >= 'a' && n <= 'f') {
                n -= ('a' - 10);
            } else {
                continue;
            }
            output[position] |= (n << nibbleshift);

            if (nibbleshift == 0) {
                position++;
            }
            nibbleshift = (nibbleshift + 4) & 7;
        }

        return output;
    }

    /**
     * 16Binary character decompression classphpinunpack
     *
     * @param is
     * @param len
     * @return
     * @throws IOException
     */
    public static String unpack(InputStream is, int len) throws IOException {
        byte[] bytes = new byte[len];
        is.read(bytes);
        return unpack(bytes);
    }

    /***
     * 16Binary character decompression classphpinunpack
     * @param bytes
     * @return
     */
    public static String unpack(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] packCa8H40(char version, byte[] nonce, String hash) {
        byte[] packed = new byte[1 + 8 + 20];
        packed[0] = (byte) version;
        System.arraycopy(nonce, 0, packed, 1, 8);
        System.arraycopy(BinHex.hex2bin(hash), 0, packed, 1 + 8, 20);
        return packed;
    }

    public static byte[] packCaH40(char version, String network, String hash) {
        byte[] packed = new byte[1 + 1 + 20];
        packed[0] = (byte) version;
        System.arraycopy(network.getBytes(), 0, packed, 1, 1);
        System.arraycopy(BinHex.hex2bin(hash), 0, packed, 1 + 1, 20);
        return packed;
    }

    public static byte[] packCaH40H8(char version, String network, String hash, String chksum) {
        byte[] packed = new byte[1 + 1 + 20 + 4];
        packed[0] = (byte) version;
        System.arraycopy(network.getBytes(), 0, packed, 1, 1);
        System.arraycopy(BinHex.hex2bin(hash), 0, packed, 1 + 1, 20);
        System.arraycopy(BinHex.hex2bin(chksum), 0, packed, 1 + 1 + 20, 4);
        return packed;
    }

    public static byte[] packCa8H40H8(char version, byte[] nonce, String hash, String chksum) {
        byte[] packed = new byte[1 + 8 + 20 + 4];
        packed[0] = (byte) version;
        System.arraycopy(nonce, 0, packed, 1, 8);
        System.arraycopy(BinHex.hex2bin(hash), 0, packed, 1 + 8, 20);
        System.arraycopy(BinHex.hex2bin(chksum), 0, packed, 1 + 8 + 20, 4);
        return packed;
    }

    public static JsonObject unpackCa8H40H8(byte[] packed) {
        JsonObject unpacked = new JsonObject();

        byte[] version = new byte[1];
        version[0] = packed[0];
        unpacked.putByte("version", version);

        byte[] nonce = new byte[8];
        System.arraycopy(packed, 1, nonce, 0, 8);
        unpacked.putByte("nonce", nonce);

        byte[] hash = new byte[20];
        System.arraycopy(packed, 1 + 8, hash, 0, 20);
        unpacked.put("keyhash", BinHex.bin2hex(hash));

        byte[] chksum = new byte[4];
        System.arraycopy(packed, 1 + 8 + 20, chksum, 0, 4);
        unpacked.put("checksum", BinHex.bin2hex(chksum));

        return unpacked;

    }

    public static byte[] packLaStar(int nonce, String seedText) {
        byte[] packed = new byte[4 + seedText.getBytes().length];

        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(nonce);
        System.arraycopy(b.array(), 0, packed, 0, 4);
        System.arraycopy(seedText.getBytes(), 0, packed, 4, seedText.getBytes().length);

        return packed;
    }
}
