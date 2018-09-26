package com.zwb.commonlibs.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.text.Charsets;

public class MD5 {
    private static ThreadLocal<MessageDigest> digestThreadLocal = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static String md5String(String str, boolean upperCase) {
        return Digits.bytesToHex(md5Bytes(str), upperCase);
    }

    public static String md5String(String str) {
        return md5String(str, false);
    }

    public static byte[] md5Bytes(String str) {
        return md5Bytes(str.getBytes(Charsets.UTF_8));
    }

    public static byte[] md5Bytes(byte[] src) {
        MessageDigest messageDigest = digestThreadLocal.get();
        messageDigest.reset();
        messageDigest.update(src);
        return messageDigest.digest();
    }
}
