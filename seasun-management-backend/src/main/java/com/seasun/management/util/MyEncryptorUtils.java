package com.seasun.management.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

import org.springframework.util.Base64Utils;

public final class MyEncryptorUtils {

    private static final String METHOD_MD5 = "MD5";
    private static final String METHOD_SHA = "SHA";

    private MyEncryptorUtils() {

    }

    /**
     * Encrypt the string by using MD5
     *
     * @param param the string need to be encrypted
     * @return the string after being encrypted if param is not null, otherwise
     * null
     */
    public static String encryptByMD5(String param) {
        return encrypt(param, METHOD_MD5);
    }

    /**
     * Encrypt the string by using SHA
     *
     * @param param the string need to be encrypted
     * @return the string after being encrypted if param is not null, otherwise
     * null
     */
    public static final String encryptBySHA(final String param) {
        return encrypt(param, METHOD_SHA);
    }

    /**
     * Encrypt by aes
     *
     * @param source the string need to be encrypted
     * @return
     * @throws Exception
     */
    public static String encryptByAES(String source) throws Exception {
        if (null != source && !source.isEmpty()) {
            return encrypt(source, DEFAULT_IV, DEFAULT_IV);
        } else {
            return null;
        }
    }

    public static String decryptByAES(String source) throws Exception {
        return decrypt(source, DEFAULT_IV, DEFAULT_IV);
    }

    /**
     * Encrypt the string with special algorithm
     *
     * @param param     the string need to be encrypted
     * @param algorithm the special algorithm <code>MD5</code>or<code>SHA</code>,
     *                  assigned by the client
     * @return the string after being encrypted if param is not null, otherwise
     * null
     */
    private static String encrypt(String param, String algorithm) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(algorithm);
            byte[] byteArray = param.getBytes("ISO-8859-1");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        char[] chArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] bytes = new char[length];
        for (int i = 0; i < bytes.length; )
            for (int rnd = random.nextInt(), n = Math.min(bytes.length - i, 8); n-- > 0; rnd >>= 4)
                bytes[i++] = chArray[rnd & 0x0000000F];
        return String.valueOf(bytes);
    }

    public static Integer getRandomNumber(Random random, int length) {
        if (random == null) {
            random = new Random();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }

        Integer integer = Integer.valueOf(builder.toString());
        if (integer.toString().length() < 6) {
            integer = Integer.valueOf(integer.toString() + random.nextInt(10));
        }
        return integer;
    }

    private static final String DEFAULT_IV = "1UZ1g_2UJXxCH#I#";

    public static String decrypt(String source, String originKey, String iv) throws Exception {
        String key = originKey;
        if (originKey == null) {
            throw new Exception("Key为空null");
        }
        // 判断Key是否为16位
        if (key.length() > 16) {
            key = key.substring(0, 16);
        } else if (key.length() < 16) {
            key = Hex.encodeHexString(DigestUtils.md5(key)).substring(0, 16);
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// "算法/模式/补码方式"
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // 先用base64解密
        byte[] base64DecryptStr = Base64.getDecoder().decode(source);

        byte[] outText = cipher.doFinal(base64DecryptStr);

        // trim去掉最后补齐的\0
        return new String(outText, "utf-8").trim();
    }

    public static String encrypt(String source, String originKey, String iv) throws Exception {
        String key = originKey;
        if (originKey == null) {
            throw new Exception("Key为空null");
        }
        // 判断Key是否为16位
        if (key.length() > 16) {
            key = key.substring(0, 16);
        } else if (key.length() < 16) {
            key = Hex.encodeHexString(DigestUtils.md5(key)).substring(0, 16);
        }

        // Cipher cipher =
        // Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");// "算法/模式/不自动补齐"
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // 因为采用NoPadding方式，需要手动补齐源数据byte数为16的整数倍，为和PHP默认补齐方式保持一致，采用\0补齐
        byte[] srcByteArray = source.getBytes("utf-8");

        // 计算需要补\0的位数
        int n = 16 - (source.getBytes("utf-8").length % 16);

        // 如果原始数据就是对齐的，则不再补齐
        if (n == 16) {
            n = 0;
        }

        byte[] byteFillZero = new byte[srcByteArray.length + n];

        System.arraycopy(srcByteArray, 0, byteFillZero, 0, srcByteArray.length);

        for (int i = srcByteArray.length; i < byteFillZero.length; i++) {
            byteFillZero[i] = (byte) '\0';
        }

        byte[] outText = cipher.doFinal(byteFillZero);

        // 此处使用BASE64做转码功能，同时能起到二次加密的作用。
        return Base64.getEncoder().encodeToString(outText);
    }

    public static String decryptByAES(String source, String originKey) throws Exception {
        String key = originKey;
        if (originKey == null) {
            throw new Exception("Key为空null");
        }
        // 判断Key是否为16位
        if (key.length() > 16) {
            key = key.substring(0, 16);
        } else if (key.length() < 16) {
            key = Hex.encodeHexString(DigestUtils.md5(key)).substring(0, 16);
        }

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
        Key keySpec = new SecretKeySpec(key.getBytes(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decryptByte = parseHexStr2Byte(source);

        byte[] outText = cipher.doFinal(decryptByte);

        String test = new String(outText);
        return test;
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 微信退款异步通知解密
     *
     * @param data 密文
     * @param key 微信apiKey
     * @return
     */
    //todo :解密失败，待研究
    public static String decryptWXRefunded(String data, String key) throws Exception {
        byte[] decode = Base64Utils.decode(data.getBytes("UTF-8"));
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(mD5Encode(key).toLowerCase().getBytes(), "AES"));
        return cipher.doFinal(decode).toString();
    }

    public static String mD5Encode(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
